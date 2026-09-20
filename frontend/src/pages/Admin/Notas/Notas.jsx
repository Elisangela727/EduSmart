import { useEffect, useState } from 'react'
import { get, post } from '../../../services/api'
import '../Alunos/Alunos.css'

const formularioInicial = {
    idMatricula: '',
    tipoAvaliacao: '',
    nota: '',
}

export default function Notas() {
    const [notas, setNotas] = useState([])
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
            const [dadosNotas, dadosMatriculas] = await Promise.all([
                get('/notas'),
                get('/matriculas'),
            ])

            setNotas(dadosNotas ?? [])
            setMatriculas(dadosMatriculas ?? [])
            setErro('')
        } catch (error) {
            setErro(error.message || 'Não foi possível carregar as notas.')
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
            const nova = await post('/notas', {
                idMatricula: Number(formulario.idMatricula),
                nota: Number(formulario.nota),
                tipoAvaliacao: formulario.tipoAvaliacao.trim(),
            })

            setNotas((atuais) => [...atuais, nova])
            setFormulario(formularioInicial)
            setMostrarFormulario(false)
            setSucesso('Nota registrada com sucesso.')
        } catch (error) {
            setErro(error.message || 'Não foi possível registrar a nota.')
        } finally {
            setSalvando(false)
        }
    }

    return (
        <div className="admin-alunos-page">
            <section className="admin-alunos-header">
                <div>
                    <span className="admin-section-label">Administração acadêmica</span>
                    <h1>Notas</h1>
                    <p>Registre e consulte as avaliações dos alunos.</p>
                </div>

                <button
                    className="admin-primary-button"
                    onClick={() => {
                        setErro('')
                        setSucesso('')
                        setMostrarFormulario(true)
                    }}
                >
                    + Nova nota
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
                        <h2>Notas registradas</h2>
                        <p>{notas.length} avaliação(ões)</p>
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
                                <th>Avaliação</th>
                                <th>Nota</th>
                            </tr>
                            </thead>

                            <tbody>
                            {notas.map((item) => (
                                <tr key={item.idNota}>
                                    <td><strong>{item.nomeAluno}</strong></td>
                                    <td>{item.nomeDisciplina}</td>
                                    <td>{item.tipoAvaliacao || '-'}</td>
                                    <td><strong>{Number(item.nota).toFixed(2)}</strong></td>
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
                                <span className="admin-section-label">Nova avaliação</span>
                                <h2>Lançar nota</h2>
                                <p>Selecione o aluno e informe a avaliação.</p>
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
                                    <label>Tipo de avaliação</label>
                                    <input
                                        maxLength="50"
                                        placeholder="Ex.: P1"
                                        value={formulario.tipoAvaliacao}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                tipoAvaliacao: e.target.value,
                                            })
                                        }
                                    />
                                </div>

                                <div className="admin-form-group">
                                    <label>Nota</label>
                                    <input
                                        type="number"
                                        min="0"
                                        max="10"
                                        step="0.01"
                                        value={formulario.nota}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                nota: e.target.value,
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
                                    {salvando ? 'Registrando...' : 'Registrar nota'}
                                </button>
                            </div>
                        </form>
                    </section>
                </div>
            )}
        </div>
    )
}