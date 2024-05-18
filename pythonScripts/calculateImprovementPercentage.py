loc = [0.6849,
       0.5984,
       0.5860,
       0.4741,
       0.7515,
       0.8621,
       0.3685,
       0.6929,
       0.4947,
       0.5319]

eloc = [0.7028,
        0.8443,
        0.5228,
        0.4608,
        0.6919,
        0.7681,
        0.5718,
        0.5298,
        0.6018,
        0.5491]

my_accuracy = [0.7666,
               0.7521,
               0.6123,
               0.5213,
               0.7849,
               0.8937,
               0.5736,
               0.7012,
               0.6375,
               0.6094]
improve1 = []
improve2 = []
for i in range(10):
    print(f"improve1 = {(my_accuracy[i] - loc[i])/loc[i]}")
    print(f"improve2 = {(my_accuracy[i] - eloc[i])/eloc[i]}")
    improve1.append((my_accuracy[i] - loc[i])/loc[i])
    improve2.append((my_accuracy[i] - eloc[i])/eloc[i])

print(improve1)
print(improve2)
ave_improve1 = sum(improve1)/10
ave_improve2 = sum(improve2)/10
print(f"ave_improve1 = {ave_improve1}")
print(f"ave_improve2 = {ave_improve2}")
