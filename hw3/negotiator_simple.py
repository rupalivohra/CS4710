from negotiator_base import BaseNegotiator
from random import random, shuffle, randint
from functools import reduce
import itertools
import math

# Example negotiator implementation, which randomly chooses to accept
# an offer or return with a randomized counteroffer.
# Important things to note: We always set self.offer to be equal to whatever
# we eventually pick as our offer. This is necessary for utility computation.
# Second, note that we ensure that we never accept an offer of "None".
class NegotiatorSimple(BaseNegotiator):
    
    def __init__(self):
        super(NegotiatorSimple, self).__init__()
        self.opponent_utility = float("inf")
        self.opponent_last_utility = float("inf")
        self.last_utility = float("inf")
        self.options = {}
        self.util_levels = []
        self.current_level = 0
        self.stepsize = 1
        self.step = 1
    
    def initialize(self, preferences, iter_limit):
        BaseNegotiator.initialize(self,preferences, iter_limit)
        self.offer = self.preferences[:]
        self.num_iters = iter_limit
        self.turn = True
        temp_options = list(itertools.permutations(self.offer,len(self.offer)))
        #print (temp_options)
        for option in temp_options:
            temp_util = int(round(self.evaluate(option),0))
            if(temp_util not in self.options.keys()):
                self.options[temp_util] = []
            self.options[temp_util].append(option)
        #print (self.options)
        self.util_levels = sorted(self.options.keys(), reverse = True)
        self.current_level = 0
        self.stepsize = math.ceil( iter_limit / len(self.util_levels))
        self.step = self.stepsize
        #print (self.options[self.current_level])

    def evaluate(self, offer):
        temp_offer = self.offer[:]
        self.offer = offer
        utils = self.utility()
        self.offer = temp_offer[:]
        return utils
    
    def make_offer(self, offer):
        current_util_level = self.util_levels[self.current_level]
        if offer and (self.evaluate(offer) >= current_util_level):
            self.offer = offer
            return offer
        self.step = self.step - 1
        if(self.step < 1):
            self.step = self.stepsize
            self.current_level = self.current_level + 1
        random_index = randint(0, len(self.options[current_util_level]) - 1)
        self.offer = list(self.options[current_util_level][random_index])
        print (self.utility())
        return self.offer
            
    def receive_utility(self, utility):
        self.opponent_last_utility = self.opponent_utility
        self.opponent_utility = utility
