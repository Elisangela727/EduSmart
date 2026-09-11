import pandas as pd
from sklearn.tree import DecisionTreeClassifier

dados = pd.read_csv("machine_learning/dados_teste.csv")

X = dados[["media_notas", "frequencia"]]
y = dados["risco"]

modelo = DecisionTreeClassifier(random_state=42)
modelo.fit(X, y)

print("Modelo treinado com sucesso!")