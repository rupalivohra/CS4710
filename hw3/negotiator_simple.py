from negotiator_base import BaseNegotiator
from random import random, shuffle, randint
from functools import reduce

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
    
    def initialize(self, preferences, iter_limit):
        BaseNegotiator.initialize(self,preferences, iter_limit)
        self.offer = self.preferences[:]
        self.num_iters = iter_limit
        self.turn = True

    def evaluate(self, offer):
        temp_offer = self.offer[:]
        self.offer = offer
        utils = self.utility()
        self.offer = temp_offer[:]
        return utils
    
    def make_offer(self, offer):
        self.last_utility = self.utility()
        self.turn = not self.turn
        if offer:
            if self.evaluate(offer) > self.last_utility:
                return offer
            if self.opponent_utility - self.opponent_last_utility < 0:
                #give a little bit
                temp_offer = self.offer[:]
                cur_util = self.evaluate(temp_offer)
                temp_offer.insert(randint(0,len(self.offer)-1),temp_offer.pop(randint(0,len(self.offer)-1)))
                temp_offer[3], temp_offer[4] = temp_offer[4], temp_offer[3]
                self.offer = temp_offer[:]
                return self.offer
            else:
                #resend previous offer
                return self.offer
        else:
            return self.preferences
            
    def receive_utility(self, utility):
        self.opponent_last_utility = self.opponent_utility
        self.opponent_utility = utility
