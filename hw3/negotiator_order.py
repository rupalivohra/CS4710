from negotiator_base import BaseNegotiator
from random import random, shuffle
import itertools

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

    def make_offer(self, offer):
        self.calculate_combos(offer)
        if self.round == 0 and offer is None:  # initial offer, we are person A
            self.player_one = True
            print("I am negotiator A")
            ordering = self.preferences[:]
            self.offer = ordering
            self.round += 1
            return self.offer
        if self.round == 0 and offer is not None:  # received an offer; we are person B
            print("I am negotiator B")
            self.offer = offer
            our_utility = self.utility()  # the utility the offer gives us
            if our_utility == self.max_util:
                return offer
            else:
                # TODO
                return offer


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
        self.opp_utilities.append(utility)

    # receive_results(self : BaseNegotiator, results : (Boolean, Float, Float, Int))
        # Store the results of the last series of negotiation (points won, success, etc.)
    def receive_results(self, results):
        pass