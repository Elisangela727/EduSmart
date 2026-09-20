import { useEffect, useState } from 'react'
import StudentLayout from '../../components/StudentLayout/StudentLayout'
import { get } from '../../services/api'

export default function Frequencias() {
    const [frequencias, setFrequencias] = useState([])
    const [carregando, setCarregando] = useState(true)
    const [erro, setErro] = useState('')

    useEffect(() => {
        carregarFrequencias()
    }, [])

    async function carregarFrequencias() {
        try {
            setCarregando(true)
            setErro('')

            const dados = await get('/frequencias/minhas')
            setFrequencias(dados ?? [])
        } catch (error) {
            setErro(
                error.message ||
                'Não foi possível carregar sua frequência.',
            )
        } finally {
            setCarregando(false)
        }
    }

    function formatarPercentual(valor) {
        return Number(valor).toLocaleString('pt-BR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
        })
    }

    return (
        <StudentLayout>
            <main className="academic-page">
                <span className="section-label">
                    Presença acadêmica
                </span>

                <h1>Minha frequência</h1>

                <p className="academic-subtitle">
                    Acompanhe sua presença nas disciplinas.
                </p>

                {erro && (
                    <div className="academic-error">{erro}</div>
                )}

                {carregando ? (
                    <div className="academic-loading">
                        Carregando frequência...
                    </div>
                ) : (
                    <section className="academic-panel">
                        {frequencias.length > 0 ? (
                            <div className="academic-table-wrapper">
                                <table className="academic-table">
                                    <thead>
                                    <tr>
                                        <th>Disciplina</th>
                                        <th>Período</th>
                                        <th>Frequência</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    {frequencias.map(
                                        (frequencia) => (
                                            <tr
                                                key={
                                                    frequencia.idFrequencia
                                                }
                                            >
                                                <td>
                                                    {
                                                        frequencia.nomeDisciplina
                                                    }
                                                </td>
                                                <td>
                                                    {frequencia.periodo ||
                                                        '--'}
                                                </td>
                                                <td>
                                                    <strong>
                                                        {formatarPercentual(
                                                            frequencia.percentual,
                                                        )}
                                                        %
                                                    </strong>
                                                </td>
                                            </tr>
                                        ),
                                    )}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <div className="academic-empty">
                                Nenhuma frequência cadastrada.
                            </div>
                        )}
                    </section>
                )}
            </main>
        </StudentLayout>
    )
}