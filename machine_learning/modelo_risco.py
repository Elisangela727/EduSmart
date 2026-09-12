import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier
dados = pd.read_csv("machine_learning/dados_teste.csv")
X = dados[["media_notas", "frequencia"]]
y = dados["risco"]
X_treino, X_teste, y_treino, y_teste = train_test_split(
    X, y, test_size=0.2, random_state=42
)
modelo = DecisionTreeClassifier()
modelo.fit(X_treino, y_treino)
acuracia = modelo.score(X_teste, y_teste)
novo_aluno = pd.DataFrame([[4.0, 65]], columns=["media_notas", "frequencia"])
resultado = modelo.predict(novo_aluno)
print("Nível de risco:", resultado[0]) 
print("Acurácia do modelo:", acuracia)   