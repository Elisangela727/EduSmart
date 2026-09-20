import { useEffect, useState } from 'react'
import StudentLayout from '../../components/StudentLayout/StudentLayout'
import { get } from '../../services/api'

export default function Analises() {
    const [analises, setAnalises] = useState([])
    const [carregando, setCarregando] = useState(true)
    const [erro, setErro] = useState('')

    useEffect(() => {
        carregarAnalises()
    }, [])

    async function carregarAnalises() {
        try {
            setCarregando(true)
            setErro('')

            const dados = await get('/analises-risco/minhas')
            setAnalises(dados ?? [])
        } catch (error) {
            setErro(
                error.message ||
                'Não foi possível carregar suas análises.',
            )
        } finally {
            setCarregando(false)
        }
    }

    function formatarData(data) {
        if (!data) {
            return '--'
        }

        const [ano, mes, dia] = data.split('-')
        return `${dia}/${mes}/${ano}`
    }

    function classeRisco(risco) {
        switch (risco?.toUpperCase()) {
            case 'BAIXO':
                return 'risco-baixo'
            case 'MEDIO':
                return 'risco-medio'
            case 'ALTO':
                return 'risco-alto'
            default:
                return 'risco-neutro'
        }
    }

    return (
        <StudentLayout>
            <main className="academic-page">
                <span className="section-label">
                    Inteligência acadêmica
                </span>

                <h1>Análises de risco</h1>

                <p className="academic-subtitle">
                    Histórico das análises realizadas pelo sistema
                    com base no seu desempenho.
                </p>

                {erro && (
                    <div className="academic-error">{erro}</div>
                )}

                {carregando ? (
                    <div className="academic-loading">
                        Carregando análises...
                    </div>
                ) : (
                    <div className="analysis-list">
                        {analises.length > 0 ? (
                            analises.map((analise) => (
                                <article
                                    className="analysis-card"
                                    key={analise.idAnalise}
                                >
                                    <div className="analysis-header">
                                        <strong
                                            className={classeRisco(
                                                analise.nivelRisco,
                                            )}
                                        >
                                            Risco{' '}
                                            {analise.nivelRisco}
                                        </strong>

                                        <span>
                                            {formatarData(
                                                analise.dataAnalise,
                                            )}
                                        </span>
                                    </div>

                                    <p>{analise.observacao}</p>
                                </article>
                            ))
                        ) : (
                            <div className="academic-panel academic-empty">
                                Nenhuma análise realizada.
                            </div>
                        )}
                    </div>
                )}
            </main>
        </StudentLayout>
    )
}