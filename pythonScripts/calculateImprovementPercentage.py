loc = [0.7691,
       0.5984,
       0.5960,
       0.4791,
       0.7515,
       0.8621,
       0.3685,
       0.7062,
       0.4947,
       0.5184,
       ]

eloc = [0.7528,
        0.8443,
        0.5481,
        0.5161,
        0.7158,
        0.7681,
        0.5718,
        0.5337,
        0.6018,
        0.5714,
        ]
my_accuracy = [0.7724,
               0.7995,
               0.6123,
               0.5188,
               0.7917,
               0.8990,
               0.7366,
               0.7084,
               0.6316,
               0.6079,
               ]
improve1 = []
improve2 = []
for i in range(10):
    # print(f"项目{i + 1}")
    # print(f"improve1 = {(my_accuracy[i] - loc[i]) / loc[i]}")
    # print(f"improve2 = {(my_accuracy[i] - eloc[i]) / eloc[i]}")
    print(f"{(my_accuracy[i] - loc[i]) / loc[i]:.4f},{(my_accuracy[i] - eloc[i]) / eloc[i]:.4f}")
    # print()
    improve1.append((my_accuracy[i] - loc[i]) / loc[i])
    improve2.append((my_accuracy[i] - eloc[i]) / eloc[i])

print(improve1)
print(improve2)
ave_improve1 = sum(improve1) / 10
ave_improve2 = sum(improve2) / 10
print(f"ave_improve1 = {ave_improve1}")
print(f"ave_improve2 = {ave_improve2}")