import { useEffect, useState } from 'react'
import { get, post } from '../../../services/api'
import '../Alunos/Alunos.css'

const formularioInicial = {
    idAluno: '',
    idDisciplina: '',
    ano: new Date().getFullYear(),
    semestre: '2',
}

export default function Matriculas() {
    const [matriculas, setMatriculas] = useState([])
    const [alunos, setAlunos] = useState([])
    const [disciplinas, setDisciplinas] = useState([])
    const [formulario, setFormulario] = useState(formularioInicial)
    const [mostrarFormulario, setMostrarFormulario] = useState(false)
    const [carregando, setCarregando] = useState(true)
    const [salvando, setSalvando] = useState(false)
    const [erro, setErro] = useState('')
    const [sucesso, setSucesso] = useState('')

    async function carregar() {
        setCarregando(true)

        try {
            const [dadosMatriculas, dadosAlunos, dadosDisciplinas] =
                await Promise.all([
                    get('/matriculas'),
                    get('/alunos'),
                    get('/disciplinas'),
                ])

            setMatriculas(dadosMatriculas ?? [])
            setAlunos(dadosAlunos ?? [])
            setDisciplinas(dadosDisciplinas ?? [])
            setErro('')
        } catch (error) {
            setErro(error.message || 'Não foi possível carregar os dados.')
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
            const nova = await post('/matriculas', {
                idAluno: Number(formulario.idAluno),
                idDisciplina: Number(formulario.idDisciplina),
                ano: Number(formulario.ano),
                semestre: Number(formulario.semestre),
            })

            setMatriculas((atuais) => [...atuais, nova])
            setFormulario(formularioInicial)
            setMostrarFormulario(false)
            setSucesso('Matrícula criada com sucesso.')
        } catch (error) {
            setErro(error.message || 'Não foi possível criar a matrícula.')
        } finally {
            setSalvando(false)
        }
    }

    return (
        <div className="admin-alunos-page">
            <section className="admin-alunos-header">
                <div>
                    <span className="admin-section-label">Administração acadêmica</span>
                    <h1>Matrículas</h1>
                    <p>Vincule alunos às disciplinas do EduSmart.</p>
                </div>

                <button
                    className="admin-primary-button"
                    onClick={() => {
                        setErro('')
                        setSucesso('')
                        setMostrarFormulario(true)
                    }}
                >
                    + Nova matrícula
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
                        <h2>Matrículas</h2>
                        <p>{matriculas.length} vínculo(s) acadêmico(s)</p>
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
                                <th>Matrícula</th>
                                <th>Disciplina</th>
                                <th>Período</th>
                            </tr>
                            </thead>

                            <tbody>
                            {matriculas.map((item) => (
                                <tr key={item.idMatricula}>
                                    <td><strong>{item.nomeAluno}</strong></td>
                                    <td>{item.matriculaAluno}</td>
                                    <td>{item.nomeDisciplina}</td>
                                    <td>{item.ano}.{item.semestre}</td>
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
                                <span className="admin-section-label">Novo vínculo</span>
                                <h2>Matricular aluno</h2>
                                <p>Escolha o aluno e a disciplina.</p>
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
                                <div className="admin-form-group">
                                    <label>Aluno</label>
                                    <select
                                        value={formulario.idAluno}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                idAluno: e.target.value,
                                            })
                                        }
                                        required
                                    >
                                        <option value="">Selecione...</option>

                                        {alunos.map((aluno) => (
                                            <option
                                                key={aluno.idAluno}
                                                value={aluno.idAluno}
                                            >
                                                {aluno.nome} - {aluno.matricula}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="admin-form-group">
                                    <label>Disciplina</label>
                                    <select
                                        value={formulario.idDisciplina}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                idDisciplina: e.target.value,
                                            })
                                        }
                                        required
                                    >
                                        <option value="">Selecione...</option>

                                        {disciplinas.map((disciplina) => (
                                            <option
                                                key={disciplina.idDisciplina}
                                                value={disciplina.idDisciplina}
                                            >
                                                {disciplina.nome}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="admin-form-group">
                                    <label>Ano</label>
                                    <input
                                        type="number"
                                        min="2000"
                                        value={formulario.ano}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                ano: e.target.value,
                                            })
                                        }
                                        required
                                    />
                                </div>

                                <div className="admin-form-group">
                                    <label>Semestre</label>
                                    <select
                                        value={formulario.semestre}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                semestre: e.target.value,
                                            })
                                        }
                                    >
                                        <option value="1">1º semestre</option>
                                        <option value="2">2º semestre</option>
                                    </select>
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
                                    {salvando ? 'Matriculando...' : 'Criar matrícula'}
                                </button>
                            </div>
                        </form>
                    </section>
                </div>
            )}
        </div>
    )
}