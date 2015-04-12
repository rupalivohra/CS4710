from negotiator_base import BaseNegotiator
from random import random, shuffle
import itertools
import decimal
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
        self.cur_round = 0
        self.player_one = False  # should be updated in first round
        self.utility_buckets = []  # stores the values of different buckets
        self.weighted_avg = {}  # stores weighted averages of each object's location in the opponent's preferences
        self.map_util_to_list = {}  # key is utility_bucket, value is list of orderings (so a list of lists)
        self.sent_offers = []  # stores all sent offers
        self.received_offers = []   # stores all received offers; keeps same indexing as opp_utilities
        self.map_opp_util = {}  # key is opp_utility, value is a list of orderings
        self.map_offer_to_potential_buckets = {}  # used to track guesses as to which bucket a given offer belongs in
        self.num_buckets = 2  # make one smaller for each utility bracket they go higher, one larger if they go down in utility, same if their utility doesn't change

    def initialize(self, preferences, iter_limit):
        BaseNegotiator.initialize(self,preferences, iter_limit)
        self.offer = self.preferences[:]
        self.max_util = round(self.utility(), 5)
        self.calculate_combos(self.preferences)
        self.set_up_weighted_avg()

    def get_utility(self, offer):
        """
        :param offer: offer in list for which you want the utility
        :return: this negotiator's utility of the parameter offer
        """
        orig_offer = self.offer
        self.offer = offer
        util = round(self.utility(), 5)
        self.offer = orig_offer
        return util

    def calculate_combos(self, scenario):  # sorts each permutation by utility in the dictionary
        perms = itertools.permutations(scenario, len(scenario))
        for perm in perms:
            perm_util = round(self.get_utility(perm),5)
            if perm_util in self.utility_buckets:
                self.map_util_to_list[perm_util].append(perm)
            else:
                self.utility_buckets.append(perm_util)
                self.map_util_to_list[perm_util] = [perm]
        self.utility_buckets = sorted(self.utility_buckets,key=None,reverse=True)

        # self.set_up_offer_map()

    # def set_up_offer_map(self):
    #     # sets the map to have as many entries as there are utility_buckets
    #     ind = "?"
    #     for i in range(0, len(self.utility_buckets)):
    #         self.map_offer_to_potential_buckets[ind] = []
    #         ind += "?"

    def set_up_weighted_avg(self):
        for val in self.preferences:
            self.weighted_avg[val] = float("inf")
        #print("the initial weighted averages",self.weighted_avg)

    def calculate_weighted_averages(self):
        """
        :return: the most likely offer based on opponent's offers and corresponding utilities thus far
        """
        # need to re-calculate based on all previous offers & new max utilities
        sum_unique_utilities = 0
        for key in self.map_opp_util.keys():
            sum_unique_utilities += key
        for val in self.weighted_avg.keys():
            self.weighted_avg[val] = 0  # clear out previous guesses
            for offer in range(0, len(self.received_offers)):
                position = self.received_offers[offer].index(val)
                utility = self.opp_utilities[offer]
                calc = (position*utility)/sum_unique_utilities
                self.weighted_avg[val] += calc
        #print("The weighted averages are", self.weighted_avg)
        ret = []
        for index in range(0, len(self.preferences)):
            cur_min = float("inf")  # target destination of the object
            cur_min_val = ""  # name of the object
            for key in self.weighted_avg:  # each object in the preferences list
                if key not in ret:
                    if self.weighted_avg[key] < cur_min:
                        cur_min_val = key
                        cur_min = self.weighted_avg[key]
                    if self.weighted_avg[key] == cur_min:
                        # assign the one that appeared in the position with overall greater utility for that offer
                        look_at_this_offer = self.map_opp_util[max(self.opp_utilities)][0]
                        #print("looking at", look_at_this_offer)
                        cur_min = look_at_this_offer.index(key)
            ret.append(cur_min_val)
        #print("The most likely set of preferences for opponent is", ret)
        return ret


    def find_highest(self, input_offer, bucket_num):
        """
        must call this before updating the round number
        :param bucket_num: the number of utility_buckets we are willing to look in; the best option from the highest_utility bucket is chosen
        :return: an offer that works best for us with the smallest hamming distance from the given offer
        """
        if bucket_num > len(self.utility_buckets) - 1:
            # ensure validity of utility_bucket
            bucket_num = len(self.utility_buckets) - 1
        if bucket_num < 1:
            bucket_num = 1

        bestOff = []
        hamming_best = float("inf")
        i = 0
        for key in self.utility_buckets:
            if i == bucket_num:
                break
            for offer in self.map_util_to_list[key]:
                #tempdist = distance.hamming(offer, self.received_offers[self.cur_round])
                tempdist = distance.hamming(offer, input_offer)
                if tempdist < hamming_best:
                    hamming_best = tempdist
                    bestOff = offer
                    #print("New best offer to counter ",self.received_offers[self.cur_round]," is ",bestOff, "with distance",hamming_best,"and utility",key)
                    #print("New best offer to counter ",input_offer," is ",bestOff, "with distance",hamming_best,"and utility",key)
            i += 1
        return bestOff

    def make_offer(self, offer):
        self.received_offers.append(offer)

        # round 1
        if self.cur_round == 0 and offer is None:  # initial offer, we are person A
            # self.calculate_combos(self.preferences)
            self.player_one = True
            print("I am negotiator A")
            ordering = self.preferences[:]
            self.offer = ordering
            self.cur_round += 1
            print("Order negotiator sending offer: ", self.offer)
            self.sent_offers.append(self.offer)
            return self.offer
        if self.cur_round == 0 and offer is not None:  # received an offer; we are person B
            # self.calculate_combos(offer)
            print("I am negotiator B")
            our_utility = round(self.get_utility(offer), 5)  # the utility the offer gives us
            if offer not in self.map_opp_util[self.opponent_utility]:
                self.map_opp_util[self.opponent_utility].append(offer)
                #print("At utility",self.opponent_utility," the offers are",self.map_opp_util[self.opponent_utility])
            #self.received_offers.append(offer)  # one offer stored potentially more than once so we can preserve order of offers
            if our_utility == self.max_util:
                self.offer = offer
                print("Order negotiator accepting offer: ", self.offer)
                self.sent_offers.append(self.offer)
                self.cur_round += 1
                return offer
            else:
                # assume offer was max_util for them (highest utility so far)
                input_offer = self.calculate_weighted_averages()
                self.offer = self.find_highest(input_offer,self.num_buckets)
                print("Order negotiator sending offer: ", self.offer)
                self.sent_offers.append(self.offer)
                self.cur_round += 1
                return self.offer

        # rounds after 1
        if offer not in self.map_opp_util[self.opponent_utility]:
            self.map_opp_util[self.opponent_utility].append(offer)
        #self.received_offers.append(offer)  # one offer stored more than once so we can preserve order of offers
        #print("round is", self.cur_round)
        if self.opp_utilities[self.cur_round] < self.opp_utilities[self.cur_round-1]:
            #  if the opponent took a hit to their utility
            #  we can assume that the offer we sent them was worse for them than what they sent back
            self.num_buckets += 1
            input_list = self.calculate_weighted_averages()
            self.offer = self.find_highest(input_list, self.num_buckets)
            # offer_as_tuple = tuple(self.sent_offers[self.cur_round-1])
        elif self.opp_utilities[self.cur_round] == self.opp_utilities[self.cur_round-1]:
            input_list = self.calculate_weighted_averages()
            self.offer = self.find_highest(input_list, self.num_buckets)
        else:
            self.num_buckets -= 1
            input_list = self.calculate_weighted_averages()
            self.offer = self.find_highest(input_list, self.num_buckets)
        print("Order negotiator sending offer: ", self.offer)
        self.sent_offers.append(self.offer)
        self.cur_round += 1
        return self.offer
        # if random() < 0.05 and offer:
        #     # Very important - we save the offer we're going to return as self.offer
        #     self.offer = offer[:]
        #     self.sent_offers.append(self.offer)
        #     self.cur_round += 1
        #     return offer
        # else:
        #     ordering = self.preferences[:]
        #     shuffle(ordering)
        #     self.offer = ordering[:]
        #     self.sent_offers.append(self.offer)
        #     self.cur_round += 1
        #     return self.offer

    # receive_utility(self : BaseNegotiator, utility : Float)
    # Store the utility the other negotiator received from their last offer

    def receive_utility(self, utility):
        utility = round(utility, 5)
        self.opponent_utility = utility
        if utility not in self.opp_utilities:
            self.map_opp_util[utility] = []
        self.opp_utilities.append(utility)

    # receive_results(self : BaseNegotiator, results : (Boolean, Float, Float, Int))
        # Store the results of the last series of negotiation (points won, success, etc.)
    def receive_results(self, results):
        pass