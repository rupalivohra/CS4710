__author__ = 'Rupali'
import itertools
import time


def calculate_combos(scenario):  # sorts each permutation by utility in the dictionary
    itertools.permutations(scenario, len(scenario))


if __name__ == "__main__":
    offer1 = ["motor"]
    offer1_longer = ["motorbike"]
    offer1_shorter = ["mo"]
    offer2 = ["motor", "bike"]
    offer3 = ["motor", "bike", "tv"]
    offer4 = ["motor", "bike", "tv", "computer"]
    offer5 = ["motor", "bike", "tv", "computer", "tv"]
    offer6 = ["motor", "bike", "tv", "computer", "tv", "laptop"]
    offer7 = ["motor", "bike", "tv", "computer", "tv", "laptop", "cellphone"]
    offer8 = ["motor", "bike", "tv", "computer", "tv", "laptop", "cellphone", "tablet"]
    offer9 = ["motor", "bike", "tv", "computer", "tv", "laptop", "cellphone", "tablet", "chair"]
    offer10 = ["motor", "bike", "tv", "computer", "tv", "laptop", "cellphone", "tablet", "chair", "charger"]
    offer11 = ["motor", "bike", "tv", "computer", "tv", "laptop", "cellphone", "tablet", "chair", "charger",
               "humidifier"]
    offer12 = ["motor", "bike", "tv", "computer", "tv", "laptop", "cellphone", "tablet", "chair", "charger",
               "humidifier", "tablecloth"]
    offer13 = ["motor", "bike", "tv", "computer", "tv", "laptop", "cellphone", "tablet", "chair", "charger",
               "humidifier", "tablecloth", "pen"]
    offer14 = ["motor", "bike", "tv", "computer", "tv", "laptop", "cellphone", "tablet", "chair", "charger",
               "humidifier", "tablecloth", "pen", "ethernet"]
    offer15 = ["motor", "bike", "tv", "computer", "tv", "laptop", "cellphone", "tablet", "chair", "charger",
               "humidifier", "tablecloth", "pen", "ethernet", "popcorn"]
    offer16 = ["motor", "bike", "tv", "computer", "tv", "laptop", "cellphone", "tablet", "chair", "charger",
               "humidifier", "tablecloth", "pen", "ethernet", "popcorn", "textbook"]
    offer1_times = []
    offer1shorter_times = []
    offer1longer_times = []
    offer5_times = []
    offer2_times = []
    offer3_times = []
    offer4_times = []
    offer6_times = []
    offer7_times = []
    offer8_times = []
    offer9_times = []
    offer10_times = []
    offer11_times = []
    offer12_times = []
    offer13_times = []
    offer14_times = []
    offer15_times = []
    offer16_times = []

    tests = [offer1, offer1_longer, offer1_shorter, offer2, offer3, offer4, offer5, offer6, offer7, offer8, offer9,
             offer10, offer11, offer12, offer13, offer14, offer15, offer16]
    times = [offer1_times, offer1longer_times, offer1shorter_times, offer2_times, offer3_times, offer4_times,
             offer5_times, offer6_times, offer7_times, offer8_times, offer9_times, offer10_times, offer11_times,
             offer12_times, offer13_times, offer14_times, offer15_times, offer16_times]

    for test in range(0, len(tests)):
        for i in range(0, 5):  # run the code segment 5 times and calc avg.
            time_before = time.clock()
            calculate_combos(tests[test])
            time_after = time.clock()
            time_difference = time_after - time_before
            times[test].append(time_difference)

        avg = 0
        for num in times[test]:
            avg += num
        avg / (len(times[test]))

        # last element in each time list is the avg
        times[test].append(avg)
        print("Average time for test", test, "is", times[test][len(times[test]) - 1])