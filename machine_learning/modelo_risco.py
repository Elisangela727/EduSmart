import pandas as pd
from sklearn.tree import DecisionTreeClassifier

dados = pd.read_csv("machine_learning/dados_teste.csv")
X = dados[["media_notas", "frequencia"]]
y = dados["risco"]

modelo = DecisionTreeClassifier()
modelo.fit(X, y)
novo_aluno = pd.DataFrame([[4.0, 65]], columns=["media_notas", "frequencia"])
resultado = modelo.predict(novo_aluno)
print("Nível de risco:", resultado[0])    