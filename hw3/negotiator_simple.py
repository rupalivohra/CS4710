from negotiator_base import BaseNegotiator
from random import random, shuffle

# Example negotiator implementation, which randomly chooses to accept
# an offer or return with a randomized counteroffer.
# Important things to note: We always set self.offer to be equal to whatever
# we eventually pick as our offer. This is necessary for utility computation.
# Second, note that we ensure that we never accept an offer of "None".
class NegotiatorSimple(BaseNegotiator):
	
	def __init__(self):
		super(NegotiatiorSimple, self).__init__()
		self.opponent_utility = float("inf")
		self.opponent_last_utility = float("inf")
		self.last_utility = float("inf")
	
	def initialize(self, preferences, iter_limit):
		Negotiator.initialize(self,preferences, iter_limit)
		self.offer = self.preferences

    def evaluate(self, offer):
        total = len(self.preferences)
        return reduce(lambda points, item: points + ((total / (offer.index(item) + 1)) - abs(offer.index(item) - self.preferences.index(item))), offer, 0)
	
    def make_offer(self, offer):
		self.last_utility = self.utility()
		if evaluate(offer) > self.last_utility:
			return offer
        if self.opponent_utility - self.opponent_last_utility < 0:
			#give a little bit
			#TODO: Write code to lower our utility by an amount less than or equal their lowered utility
			pass
		else
			#resend previous offer
			return self.offer
			
	def receive_utility(self, utility):
		self.opponent_last_utility = self.opponent_utility
		self.opponent_utility = utility
