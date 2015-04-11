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
class NegotiatorProb(BaseNegotiator):
    
    def __init__(self):
        super(NegotiatorProb, self).__init__()
        self.opponent_utility = float("inf")
        self.opponent_last_utility = float("inf")
        self.last_utility = float("inf")
        self.options = {}
        self.util_levels = []
        self.current_level = 0
        self.stepsize = 1
        self.step = 1
        self.proposed_offers = []
        self.opponent_up = 0
        self.opponent_down = 0
        self.opponent_stays = 0
        self.opponent_moves = 0
        self.us_up = 0
        self.us_down = 0
        self.us_same = 0
        #Add code to track the behavior we made before a turn and the behavior they made before a turn, then sort the counts by that
    
    def initialize(self, preferences, iter_limit):
        BaseNegotiator.initialize(self,preferences, iter_limit)
        self.offer = self.preferences[:]
        self.num_iters = iter_limit
        self.proposed_offers = []
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
        self.proposed_offers.append(offer)
        current_util_level = self.util_levels[self.current_level]
        
        #take any offer that is better than what we're looking for
        if offer and (self.evaluate(offer) >= current_util_level):
            self.offer = offer
            return offer
        
        if((self.opponent_up / self.opponent_moves) >= (self.opponent_down / self.opponent_moves)) and ((self.opponent_up / self.opponent_moves) >= (self.opponent_stays / self.opponent_moves)):
            if(self.current_level > 0):
                print ("UP")
                self.current_level -= 1        
        elif((self.opponent_down / self.opponent_moves) >= (self.opponent_stays / self.opponent_moves)):
            if(self.current_level < len(self.util_levels) - 1):
                print ("DOWN")
                self.current_level += 1
        #else stay where we are
        
        random_index = randint(0, len(self.options[current_util_level]) - 1)
        self.offer = list(self.options[current_util_level][random_index])
        print (self.utility())
        return self.offer
            
    def receive_utility(self, utility):
        self.opponent_last_utility = self.opponent_utility
        self.opponent_utility = utility
        self.opponent_moves += 1
        if((self.opponent_utility - self.opponent_last_utility) > .005):
            self.opponent_up += 1
        elif((self.opponent_utility - self.opponent_last_utility) < -.005):
            self.opponent_down += 1
        else:
            self.opponent_stays += 1
