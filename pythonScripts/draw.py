import matplotlib.pyplot as plt
import pandas as pd

file_path = 'E:/IDEA/maven-project/DeveloperContributionEvaluation/guice_result.csv'
data = pd.read_csv(file_path)
plt.hist(data['score'], bins=10, color='pink', edgecolor='b')
plt.show()