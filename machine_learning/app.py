import os

from flask import Flask, jsonify, request

from modelo_risco import prever_risco

app = Flask(__name__)


@app.get("/health")
def health():
    return jsonify({
        "status": "UP",
        "servico": "EduSmart Machine Learning"
    })


@app.post("/predict")
def predict():
    dados = request.get_json(silent=True)

    if not dados:
        return jsonify({
            "erro": "Corpo da requisição não informado"
        }), 400

    if "media_notas" not in dados or "frequencia" not in dados:
        return jsonify({
            "erro": "media_notas e frequencia são obrigatórios"
        }), 400

    try:
        media_notas = float(dados["media_notas"])
        frequencia = float(dados["frequencia"])
    except (TypeError, ValueError):
        return jsonify({
            "erro": "media_notas e frequencia devem ser números"
        }), 400

    if media_notas < 0 or media_notas > 10:
        return jsonify({
            "erro": "media_notas deve estar entre 0 e 10"
        }), 400

    if frequencia < 0 or frequencia > 100:
        return jsonify({
            "erro": "frequencia deve estar entre 0 e 100"
        }), 400

    nivel_risco = prever_risco(
        media_notas,
        frequencia
    )

    return jsonify({
        "nivel_risco": nivel_risco
    })


if __name__ == "__main__":
    port = int(os.environ.get("PORT", 5000))

    app.run(
        host="0.0.0.0",
        port=port,
        debug=False
    )