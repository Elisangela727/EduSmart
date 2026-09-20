import os

import pandas as pd
from sklearn.tree import DecisionTreeClassifier

CAMINHO_ATUAL = os.path.dirname(os.path.abspath(__file__))
CAMINHO_DADOS = os.path.join(CAMINHO_ATUAL, "dados_teste.csv")

dados = pd.read_csv(CAMINHO_DADOS)

X = dados[["media_notas", "frequencia"]]
y = dados["risco"]

modelo = DecisionTreeClassifier(random_state=42)
modelo.fit(X, y)


def prever_risco(media_notas, frequencia):
    novo_aluno = pd.DataFrame(
        [[media_notas, frequencia]],
        columns=["media_notas", "frequencia"]
    )

    resultado = modelo.predict(novo_aluno)[0]

    return resultado.upper()