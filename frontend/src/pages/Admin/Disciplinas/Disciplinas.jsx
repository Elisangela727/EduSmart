import { useEffect, useMemo, useState } from 'react'
import { get, post } from '../../../services/api'
import '../Alunos/Alunos.css'

const formularioInicial = {
    nome: '',
    cargaHoraria: '',
}

export default function Disciplinas() {
    const [disciplinas, setDisciplinas] = useState([])
    const [formulario, setFormulario] = useState(formularioInicial)
    const [busca, setBusca] = useState('')
    const [carregando, setCarregando] = useState(true)
    const [salvando, setSalvando] = useState(false)
    const [mostrarFormulario, setMostrarFormulario] = useState(false)
    const [erro, setErro] = useState('')
    const [sucesso, setSucesso] = useState('')

    async function carregar() {
        setCarregando(true)

        try {
            const resposta = await get('/disciplinas')
            setDisciplinas(Array.isArray(resposta) ? resposta : [])
            setErro('')
        } catch (error) {
            setErro(error.message || 'Não foi possível carregar as disciplinas.')
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        carregar()
    }, [])

    const filtradas = useMemo(() => {
        const termo = busca.trim().toLowerCase()

        if (!termo) return disciplinas

        return disciplinas.filter((disciplina) =>
            disciplina.nome?.toLowerCase().includes(termo),
        )
    }, [disciplinas, busca])

    async function cadastrar(event) {
        event.preventDefault()
        setSalvando(true)
        setErro('')
        setSucesso('')

        try {
            const nova = await post('/disciplinas', {
                nome: formulario.nome.trim(),
                cargaHoraria: Number(formulario.cargaHoraria),
            })

            setDisciplinas((atuais) => [...atuais, nova])
            setFormulario(formularioInicial)
            setMostrarFormulario(false)
            setSucesso('Disciplina cadastrada com sucesso.')
        } catch (error) {
            setErro(error.message || 'Não foi possível cadastrar a disciplina.')
        } finally {
            setSalvando(false)
        }
    }

    return (
        <div className="admin-alunos-page">
            <section className="admin-alunos-header">
                <div>
                    <span className="admin-section-label">Administração acadêmica</span>
                    <h1>Disciplinas</h1>
                    <p>Gerencie as disciplinas disponíveis no EduSmart.</p>
                </div>

                <button
                    className="admin-primary-button"
                    onClick={() => {
                        setFormulario(formularioInicial)
                        setErro('')
                        setSucesso('')
                        setMostrarFormulario(true)
                    }}
                >
                    + Nova disciplina
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
                        <h2>Disciplinas cadastradas</h2>
                        <p>{disciplinas.length} disciplina(s)</p>
                    </div>

                    <div className="admin-search-field">
                        <input
                            type="search"
                            placeholder="Buscar disciplina..."
                            value={busca}
                            onChange={(e) => setBusca(e.target.value)}
                        />
                    </div>
                </div>

                {carregando ? (
                    <div className="admin-alunos-loading">
                        Carregando disciplinas...
                    </div>
                ) : (
                    <div className="admin-table-wrapper">
                        <table className="admin-table">
                            <thead>
                            <tr>
                                <th>Disciplina</th>
                                <th>Carga horária</th>
                            </tr>
                            </thead>

                            <tbody>
                            {filtradas.map((disciplina) => (
                                <tr key={disciplina.idDisciplina}>
                                    <td><strong>{disciplina.nome}</strong></td>
                                    <td>{disciplina.cargaHoraria} horas</td>
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
                                <span className="admin-section-label">Novo cadastro</span>
                                <h2>Cadastrar disciplina</h2>
                                <p>Informe os dados da disciplina.</p>
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
                                    <label>Nome</label>
                                    <input
                                        value={formulario.nome}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                nome: e.target.value,
                                            })
                                        }
                                        maxLength="100"
                                        required
                                    />
                                </div>

                                <div className="admin-form-group">
                                    <label>Carga horária</label>
                                    <input
                                        type="number"
                                        min="1"
                                        value={formulario.cargaHoraria}
                                        onChange={(e) =>
                                            setFormulario({
                                                ...formulario,
                                                cargaHoraria: e.target.value,
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
                                    {salvando ? 'Cadastrando...' : 'Cadastrar disciplina'}
                                </button>
                            </div>
                        </form>
                    </section>
                </div>
            )}
        </div>
    )
}