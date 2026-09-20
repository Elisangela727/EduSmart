import { useEffect, useState } from 'react'
import StudentLayout from '../../components/StudentLayout/StudentLayout'
import { get, patch } from '../../services/api'

export default function Alertas() {
    const [alertas, setAlertas] = useState([])
    const [carregando, setCarregando] = useState(true)
    const [erro, setErro] = useState('')

    useEffect(() => {
        carregarAlertas()
    }, [])

    async function carregarAlertas() {
        try {
            setCarregando(true)
            setErro('')

            const dados = await get('/alertas/meus')
            setAlertas(dados ?? [])
        } catch (error) {
            setErro(
                error.message ||
                'Não foi possível carregar seus alertas.',
            )
        } finally {
            setCarregando(false)
        }
    }

    async function visualizar(idAlerta) {
        try {
            const atualizado = await patch(
                `/alertas/meus/${idAlerta}/visualizar`,
            )

            setAlertas((atuais) =>
                atuais.map((alerta) =>
                    alerta.idAlerta === idAlerta
                        ? atualizado
                        : alerta,
                ),
            )
        } catch (error) {
            setErro(
                error.message ||
                'Não foi possível atualizar o alerta.',
            )
        }
    }

    function formatarData(data) {
        if (!data) {
            return '--'
        }

        const [ano, mes, dia] = data.split('-')
        return `${dia}/${mes}/${ano}`
    }

    return (
        <StudentLayout>
            <main className="academic-page">
                <span className="section-label">
                    Acompanhamento acadêmico
                </span>

                <h1>Meus alertas</h1>

                <p className="academic-subtitle">
                    Recomendações e avisos gerados a partir das
                    análises do seu desempenho.
                </p>

                {erro && (
                    <div className="academic-error">{erro}</div>
                )}

                {carregando ? (
                    <div className="academic-loading">
                        Carregando alertas...
                    </div>
                ) : (
                    <div className="analysis-list">
                        {alertas.length > 0 ? (
                            alertas.map((alerta) => (
                                <article
                                    className={`analysis-card ${
                                        alerta.visualizado
                                            ? 'academic-read'
                                            : ''
                                    }`}
                                    key={alerta.idAlerta}
                                >
                                    <div className="analysis-header">
                                        <strong>
                                            {alerta.tipoAlerta?.replace(
                                                'RISCO_',
                                                'Risco ',
                                            )}
                                        </strong>

                                        <span>
                                            {formatarData(
                                                alerta.dataAlerta,
                                            )}
                                        </span>
                                    </div>

                                    <p>{alerta.mensagem}</p>

                                    {!alerta.visualizado && (
                                        <button
                                            className="academic-action"
                                            type="button"
                                            onClick={() =>
                                                visualizar(
                                                    alerta.idAlerta,
                                                )
                                            }
                                        >
                                            Marcar como visualizado
                                        </button>
                                    )}
                                </article>
                            ))
                        ) : (
                            <div className="academic-panel academic-empty">
                                Nenhum alerta encontrado.
                            </div>
                        )}
                    </div>
                )}
            </main>
        </StudentLayout>
    )
}