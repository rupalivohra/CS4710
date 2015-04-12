__author__ = 'Rupali'
import itertools
import time


def calculate_combos(scenario):  # sorts each permutation by utility in the dictionary
    my_list = list(itertools.permutations(scenario, len(scenario)))
    return len(my_list)


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

    tests = [offer1, offer1_longer, offer1_shorter, offer2, offer3, offer4, offer5, offer6, offer7, offer8, offer9,
             offer10]
    times = [offer1_times, offer1longer_times, offer1shorter_times, offer2_times, offer3_times, offer4_times,
             offer5_times, offer6_times, offer7_times, offer8_times, offer9_times, offer10_times]

    for test in range(0, len(tests)):
        for i in range(0, 5):  # run the code segment 5 times and calc avg.
            time_before = time.clock()
            temp = calculate_combos(tests[test])
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