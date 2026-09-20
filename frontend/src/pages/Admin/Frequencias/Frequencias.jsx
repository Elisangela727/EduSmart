import { useEffect, useState } from 'react'
import { get, post } from '../../../services/api'
import '../Alunos/Alunos.css'

const formularioInicial = {
    idMatricula: '',
    percentual: '',
    periodo: '',
}

export default function Frequencias() {
    const [frequencias, setFrequencias] = useState([])
    const [matriculas, setMatriculas] = useState([])
    const [formulario, setFormulario] = useState(formularioInicial)
    const [mostrarFormulario, setMostrarFormulario] = useState(false)
    const [carregando, setCarregando] = useState(true)
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')
    const [sucesso, setSucesso] = useState('')

    async function carregar() {
        setCarregando(true)

        try {
            const [dadosFrequencias, dadosMatriculas] = await Promise.all([
                get('/frequencias'),
                get('/matriculas'),
            ])

            setFrequencias(dadosFrequencias ?? [])
            setMatriculas(dadosMatriculas ?? [])
            setErro('')
        } catch (error) {
            setErro(error.message || 'Não foi possível carregar as frequências.')
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        carregar()
    }, [])

    async function cadastrar(event) {
        event.preventDefault()
        setSalvando(true)
        setErro('')
        setSucesso('')

        try {
            const nova = await post('/frequencias', {
                idMatricula: Number(formulario.idMatricula),
                percentual: Number(formulario.percentual),
                periodo: formulario.periodo.trim(),
            })

            setFrequencias((atuais) => [...atuais, nova])
            setFormulario(formularioInicial)
            setMostrarFormulario(false)
            setSucesso('Frequência registrada com sucesso.')
        } catch (error) {
            setErro(error.message || 'Não foi possível registrar a frequência.')
        } finally {
            setSalvando(false)
        }
    }

    return (
        <div className="admin-alunos-page">
            <section className="admin-alunos-header">
                <div>
                    <span className="admin-section-label">Administração acadêmica</span>
                    <h1>Frequências</h1>
                    <p>Registre e acompanhe a frequência acadêmica dos alunos.</p>
                </div>

                <button
                    className="admin-primary-button"
                    onClick={() => {
                        setErro('')
                        setSucesso('')
                        setMostrarFormulario(true)
                    }}
                >
                    + Nova frequência
                </button>
            </section>

            {sucesso && (
                <div className="admin-alunos-message admin-alunos-success">
                    {sucesso}
                </div>
            )}

            {erro && !mostrarFormulario && (
                <div className="admin-alunos-message admin-alunos-error">
                    {erro}
                </div>
            )}

            <section className="admin-alunos-panel">
                <div className="admin-alunos-toolbar">
                    <div>
                        <h2>Frequências registradas</h2>
                        <p>{frequencias.length} registro(s)</p>
                    </div>
                </div>

                {carregando ? (
                    <div className="admin-alunos-loading">Carregando...</div>
                ) : (
                    <div className="admin-table-wrapper">
                        <table className="admin-table">
                            <thead>
                            <tr>
                                <th>Aluno</th>
                                <th>Disciplina</th>
                                <th>Período</th>
                                <th>Frequência</th>
                            </tr>
                            </thead>

                            <tbody>
                            {frequencias.map((item) => (
                                <tr key={item.idFrequencia}>
                                    <td><strong>{item.nomeAluno}</strong></td>
                                    <td>{item.nomeDisciplina}</td>
                                    <td>{item.periodo || '-'}</td>
                                    <td><strong>{Number(item.percentual).toFixed(2)}%</strong></td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>

            {mostrarFormulario && (
                <div className="admin-modal-backdrop">
                    <section className="admin-modal">
                        <div className="admin-modal-header">
                            <div>
                                <span className="admin-section-label">Novo registro</span>
                                <h2>Registrar frequência</h2>
                                <p>Informe a frequência do aluno na disciplina.</p>
                            </div>

                            <button
                                className="admin-modal-close"
                                onClick={() => setMostrarFormulario(false)}
                            >
                                ×
                            </button>
                        </div>

                        <form className="admin-student-form" onSubmit={cadastrar}>
                            <div className="admin-form-grid">
                                <div className="admin-form-group admin-form-full">
                                    <label>Aluno / Disciplina</label>

                                    <select
                                        value={formulario.idMatricula}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                idMatricula: e.target.value,
                                            })
                                        }
                                        required
                                    >
                                        <option value="">Selecione...</option>

                                        {matriculas.map((item) => (
                                            <option
                                                key={item.idMatricula}
                                                value={item.idMatricula}
                                            >
                                                {item.nomeAluno} — {item.nomeDisciplina}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="admin-form-group">
                                    <label>Período</label>
                                    <input
                                        maxLength="30"
                                        placeholder="Ex.: 2026.2"
                                        value={formulario.periodo}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                periodo: e.target.value,
                                            })
                                        }
                                    />
                                </div>

                                <div className="admin-form-group">
                                    <label>Frequência (%)</label>
                                    <input
                                        type="number"
                                        min="0"
                                        max="100"
                                        step="0.01"
                                        value={formulario.percentual}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                percentual: e.target.value,
                                            })
                                        }
                                        required
                                    />
                                </div>
                            </div>

                            {erro && <div className="admin-form-error">{erro}</div>}

                            <div className="admin-form-actions">
                                <button
                                    type="button"
                                    className="admin-secondary-button"
                                    onClick={() => setMostrarFormulario(false)}
                                >
                                    Cancelar
                                </button>

                                <button
                                    className="admin-primary-button"
                                    disabled={salvando}
                                >
                                    {salvando ? 'Registrando...' : 'Registrar frequência'}
                                </button>
                            </div>
                        </form>
                    </section>
                </div>
            )}
        </div>
    )
}