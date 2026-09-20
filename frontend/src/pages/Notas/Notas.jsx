import { useEffect, useState } from 'react'
import StudentLayout from '../../components/StudentLayout/StudentLayout'
import { get } from '../../services/api'

export default function Notas() {
    const [notas, setNotas] = useState([])
    const [carregando, setCarregando] = useState(true)
    const [erro, setErro] = useState('')

    useEffect(() => {
        carregarNotas()
    }, [])

    async function carregarNotas() {
        try {
            setCarregando(true)
            setErro('')

            const dados = await get('/notas/minhas')
            setNotas(dados ?? [])
        } catch (error) {
            setErro(
                error.message ||
                'Não foi possível carregar suas notas.',
            )
        } finally {
            setCarregando(false)
        }
    }

    function formatarNota(valor) {
        return Number(valor).toLocaleString('pt-BR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
        })
    }

    return (
        <StudentLayout>
            <main className="academic-page">
                <span className="section-label">
                    Desempenho acadêmico
                </span>

                <h1>Minhas notas</h1>

                <p className="academic-subtitle">
                    Consulte suas avaliações e resultados por
                    disciplina.
                </p>

                {erro && (
                    <div className="academic-error">{erro}</div>
                )}

                {carregando ? (
                    <div className="academic-loading">
                        Carregando notas...
                    </div>
                ) : (
                    <section className="academic-panel">
                        {notas.length > 0 ? (
                            <div className="academic-table-wrapper">
                                <table className="academic-table">
                                    <thead>
                                    <tr>
                                        <th>Disciplina</th>
                                        <th>Avaliação</th>
                                        <th>Nota</th>
                                    </tr>
                                    </thead>

                                    <tbody>
                                    {notas.map((nota) => (
                                        <tr key={nota.idNota}>
                                            <td>
                                                {
                                                    nota.nomeDisciplina
                                                }
                                            </td>
                                            <td>
                                                {nota.tipoAvaliacao ||
                                                    'Avaliação'}
                                            </td>
                                            <td>
                                                <strong>
                                                    {formatarNota(
                                                        nota.nota,
                                                    )}
                                                </strong>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        ) : (
                            <div className="academic-empty">
                                Nenhuma nota cadastrada.
                            </div>
                        )}
                    </section>
                )}
            </main>
        </StudentLayout>
    )
}