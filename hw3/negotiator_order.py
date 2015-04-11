from negotiator_base import BaseNegotiator
from random import random, shuffle
import itertools
import distance

# Example negotiator implementation, which randomly chooses to accept
# an offer or return with a randomized counteroffer.
# Important things to note: We always set self.offer to be equal to whatever
# we eventually pick as our offer. This is necessary for utility computation.
# Second, note that we ensure that we never accept an offer of "None".


class NegotiatorOrder(BaseNegotiator):
    # Override the make_offer method from BaseNegotiator to accept a given offer 5%
    # of the time, and return a random permutation the rest of the time.
    def __init__(self):
        super(NegotiatorOrder, self).__init__()
        self.max_util = 0
        self.opponent_utility = float("inf")  # the most recently received utility
        self.opp_utilities = []  # a list in chronological order of opponent utilities
        self.round = 0
        self.player_one = False  # should be updated in first round
        self.utility_buckets = []  # stores the values of different buckets
        self.map_util_to_list = {}  # key is utility_bucket, value is list of orderings (so a list of lists)
        self.sent_offers = []  # stores all sent offers
        self.received_offers = []   #stores all received offers
        self.map_opp_util = {}  # key is opp_utility, value is a list of orderings
        self.map_offer_to_potential_buckets = {}  # used to track guesses as to which bucket a given offer belongs in

    def initialize(self, preferences, iter_limit):
        BaseNegotiator.initialize(self,preferences, iter_limit)
        self.offer = self.preferences[:]
        self.max_util = self.utility()

    def get_utility(self, offer):
        """
        :param offer: offer in list for which you want the utility
        :return: this negotiator's utility of the parameter offer
        """
        orig_offer = self.offer
        self.offer = offer
        util = self.utility()
        self.offer = orig_offer
        return util

    def calculate_combos(self, scenario):  # sorts each permutation by utility in the dictionary
        perms = itertools.permutations(scenario, len(scenario))
        for perm in perms:
            perm_util = self.get_utility(perm)
            if perm_util in self.utility_buckets:
                self.map_util_to_list[perm_util].append(perm)
            else:
                self.utility_buckets.append(perm_util)
                self.map_util_to_list[perm_util] = [perm]
        self.utility_buckets.sort()
        # self.set_up_offer_map()

    # def set_up_offer_map(self):
    #     # sets the map to have as many entries as there are utility_buckets
    #     ind = "?"
    #     for i in range(0, len(self.utility_buckets)):
    #         self.map_offer_to_potential_buckets[ind] = []
    #         ind += "?"

    def make_offer(self, offer):
        self.received_offers.append(offer)

        # round 1
        if self.round == 0 and offer is None:  # initial offer, we are person A
            self.calculate_combos(self.preferences)
            self.player_one = True
            print("I am negotiator A")
            ordering = self.preferences[:]
            self.offer = ordering
            self.round += 1
            print("Order negotiator sending offer: ", self.offer)
            self.sent_offers.append(self.offer)
            return self.offer
        if self.round == 0 and offer is not None:  # received an offer; we are person B
            self.calculate_combos(offer)
            print("I am negotiator B")
            self.round += 1
            our_utility = self.get_utility(offer)  # the utility the offer gives us
            if offer not in self.received_offers:
                self.map_opp_util[self.opponent_utility] = [offer]
            self.received_offers.append(offer)  # one offer stored more than once so we can preserve order of offers
            if our_utility == self.max_util:
                self.offer = offer
                print("Order negotiator accepting offer: ", self.offer)
                self.sent_offers.append(self.offer)
                return offer
            else:
                self.offer = self.preferences
                print("Order negotiator sending offer: ", self.offer)
                self.sent_offers.append(self.offer)
                return offer

        # rounds after 2
        if offer not in self.received_offers:
            self.map_opp_util[self.opponent_utility] = [offer]
            self.received_offers.append(offer)  # one offer stored more than once so we can preserve order of offers

        if self.opp_utilities[self.round] < self.opp_utilities[self.round-1]:
            #  if the opponent took a hit to their utility
            #  we can assume that the offer we sent them was worse for them than what they sent back
            offer_as_tuple = tuple(self.sent_offers[self.round-1])
            self.map_offer_to_potential_buckets[offer_as_tuple] =


        if random() < 0.05 and offer:
            # Very important - we save the offer we're going to return as self.offer
            self.offer = offer[:]
            self.round += 1
            return offer
        else:
            ordering = self.preferences[:]
            shuffle(ordering)
            self.offer = ordering[:]
            self.round += 1
            return self.offer


    # receive_utility(self : BaseNegotiator, utility : Float)
    # Store the utility the other negotiator received from their last offer
    def receive_utility(self, utility):
        self.opponent_utility = utility
        if utility not in self.opp_utilities:
            self.opp_utilities.append(utility)

    # receive_results(self : BaseNegotiator, results : (Boolean, Float, Float, Int))
        # Store the results of the last series of negotiation (points won, success, etc.)
    def receive_results(self, results):
        pass