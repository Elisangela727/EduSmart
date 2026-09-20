import { useEffect, useMemo, useState } from 'react'
import { get, post } from '../../../services/api'
import './Alunos.css'

const formularioInicial = {
    nome: '',
    email: '',
    senha: '',
    matricula: '',
    curso: '',
    semestre: '',
}

export default function Alunos() {
    const [alunos, setAlunos] = useState([])
    const [formulario, setFormulario] = useState(formularioInicial)
    const [carregando, setCarregando] = useState(true)
    const [salvando, setSalvando] = useState(false)
    const [mostrarFormulario, setMostrarFormulario] = useState(false)
    const [mostrarSenha, setMostrarSenha] = useState(false)
    const [busca, setBusca] = useState('')
    const [erro, setErro] = useState('')
    const [sucesso, setSucesso] = useState('')

    async function carregarAlunos() {
        try {
            setCarregando(true)
            setErro('')

            const resposta = await get('/alunos')
            setAlunos(resposta ?? [])
        } catch (error) {
            setErro(
                error.message ||
                'Não foi possível carregar os alunos.',
            )
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        carregarAlunos()
    }, [])

    const alunosFiltrados = useMemo(() => {
        const termo = busca.trim().toLowerCase()

        if (!termo) {
            return alunos
        }

        return alunos.filter((aluno) => {
            return [
                aluno.nome,
                aluno.email,
                aluno.matricula,
                aluno.curso,
                String(aluno.semestre ?? ''),
            ].some((valor) =>
                String(valor ?? '')
                    .toLowerCase()
                    .includes(termo),
            )
        })
    }, [alunos, busca])

    function alterarCampo(event) {
        const { name, value } = event.target

        setFormulario((atual) => ({
            ...atual,
            [name]: value,
        }))
    }

    function abrirFormulario() {
        setFormulario(formularioInicial)
        setErro('')
        setSucesso('')
        setMostrarSenha(false)
        setMostrarFormulario(true)
    }

    function fecharFormulario() {
        if (salvando) {
            return
        }

        setMostrarFormulario(false)
        setFormulario(formularioInicial)
        setErro('')
    }

    async function cadastrarAluno(event) {
        event.preventDefault()

        setErro('')
        setSucesso('')
        setSalvando(true)

        try {
            const novoAluno = await post(
                '/alunos/cadastro-completo',
                {
                    nome: formulario.nome.trim(),
                    email: formulario.email.trim(),
                    senha: formulario.senha,
                    matricula: formulario.matricula.trim(),
                    curso: formulario.curso.trim(),
                    semestre: Number(formulario.semestre),
                },
            )

            setAlunos((atuais) => [
                ...atuais,
                novoAluno,
            ])

            setFormulario(formularioInicial)
            setMostrarFormulario(false)
            setMostrarSenha(false)

            setSucesso('Aluno cadastrado com sucesso.')
        } catch (error) {
            setErro(
                error.message ||
                'Não foi possível cadastrar o aluno.',
            )
        } finally {
            setSalvando(false)
        }
    }

    return (
        <div className="admin-alunos-page">
            <section className="admin-alunos-header">
                <div>
                    <span className="admin-section-label">
                        Administração acadêmica
                    </span>

                    <h1>Alunos</h1>

                    <p>
                        Consulte os alunos cadastrados e adicione
                        novos estudantes ao EduSmart.
                    </p>
                </div>

                <button
                    type="button"
                    className="admin-primary-button"
                    onClick={abrirFormulario}
                >
                    + Novo aluno
                </button>
            </section>

            {erro && !mostrarFormulario && (
                <div className="admin-alunos-message admin-alunos-error">
                    <span>{erro}</span>

                    <button
                        type="button"
                        onClick={carregarAlunos}
                    >
                        Tentar novamente
                    </button>
                </div>
            )}

            {sucesso && (
                <div className="admin-alunos-message admin-alunos-success">
                    {sucesso}
                </div>
            )}

            <section className="admin-alunos-panel">
                <div className="admin-alunos-toolbar">
                    <div>
                        <h2>Alunos cadastrados</h2>

                        <p>
                            {alunos.length}{' '}
                            {alunos.length === 1
                                ? 'aluno cadastrado'
                                : 'alunos cadastrados'}
                        </p>
                    </div>

                    <div className="admin-search-field">
                        <input
                            type="search"
                            placeholder="Buscar por nome, matrícula ou curso..."
                            value={busca}
                            onChange={(event) =>
                                setBusca(event.target.value)
                            }
                        />
                    </div>
                </div>

                {carregando ? (
                    <div className="admin-alunos-loading">
                        <div className="admin-dashboard-spinner" />
                        <span>Carregando alunos...</span>
                    </div>
                ) : alunosFiltrados.length === 0 ? (
                    <div className="admin-alunos-empty">
                        <strong>Nenhum aluno encontrado</strong>

                        <p>
                            {busca
                                ? 'Tente utilizar outro termo de busca.'
                                : 'Cadastre o primeiro aluno do sistema.'}
                        </p>
                    </div>
                ) : (
                    <div className="admin-table-wrapper">
                        <table className="admin-table">
                            <thead>
                            <tr>
                                <th>Aluno</th>
                                <th>Matrícula</th>
                                <th>Curso</th>
                                <th>Semestre</th>
                            </tr>
                            </thead>

                            <tbody>
                            {alunosFiltrados.map((aluno) => (
                                <tr key={aluno.idAluno}>
                                    <td>
                                        <div className="admin-student-cell">
                                            <div className="admin-student-avatar">
                                                {aluno.nome
                                                        ?.charAt(0)
                                                        .toUpperCase() ||
                                                    'A'}
                                            </div>

                                            <div>
                                                <strong>
                                                    {aluno.nome}
                                                </strong>

                                                <span>
                                                        {aluno.email}
                                                    </span>
                                            </div>
                                        </div>
                                    </td>

                                    <td>
                                            <span className="admin-matricula-badge">
                                                {aluno.matricula}
                                            </span>
                                    </td>

                                    <td>{aluno.curso}</td>

                                    <td>
                                        {aluno.semestre}º semestre
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </section>

            {mostrarFormulario && (
                <div
                    className="admin-modal-backdrop"
                    onMouseDown={(event) => {
                        if (event.target === event.currentTarget) {
                            fecharFormulario()
                        }
                    }}
                >
                    <section
                        className="admin-modal"
                        role="dialog"
                        aria-modal="true"
                        aria-labelledby="novo-aluno-titulo"
                    >
                        <div className="admin-modal-header">
                            <div>
                                <span className="admin-section-label">
                                    Novo cadastro
                                </span>

                                <h2 id="novo-aluno-titulo">
                                    Cadastrar aluno
                                </h2>

                                <p>
                                    Informe os dados de acesso e os
                                    dados acadêmicos do estudante.
                                </p>
                            </div>

                            <button
                                type="button"
                                className="admin-modal-close"
                                onClick={fecharFormulario}
                                disabled={salvando}
                                aria-label="Fechar"
                            >
                                ×
                            </button>
                        </div>

                        <form
                            className="admin-student-form"
                            onSubmit={cadastrarAluno}
                        >
                            <div className="admin-form-section">
                                <div className="admin-form-section-heading">
                                    <span>01</span>

                                    <div>
                                        <strong>Dados de acesso</strong>
                                        <p>
                                            Informações utilizadas para
                                            entrar no EduSmart.
                                        </p>
                                    </div>
                                </div>

                                <div className="admin-form-grid">
                                    <div className="admin-form-group admin-form-full">
                                        <label htmlFor="nome">
                                            Nome completo
                                        </label>

                                        <input
                                            id="nome"
                                            name="nome"
                                            type="text"
                                            maxLength="100"
                                            value={formulario.nome}
                                            onChange={alterarCampo}
                                            placeholder="Nome do aluno"
                                            required
                                        />
                                    </div>

                                    <div className="admin-form-group">
                                        <label htmlFor="email">
                                            E-mail
                                        </label>

                                        <input
                                            id="email"
                                            name="email"
                                            type="email"
                                            maxLength="100"
                                            value={formulario.email}
                                            onChange={alterarCampo}
                                            placeholder="aluno@exemplo.com"
                                            required
                                        />
                                    </div>

                                    <div className="admin-form-group">
                                        <label htmlFor="senha">
                                            Senha inicial
                                        </label>

                                        <div className="admin-password-field">
                                            <input
                                                id="senha"
                                                name="senha"
                                                type={
                                                    mostrarSenha
                                                        ? 'text'
                                                        : 'password'
                                                }
                                                minLength="6"
                                                maxLength="100"
                                                value={formulario.senha}
                                                onChange={alterarCampo}
                                                placeholder="Mínimo 6 caracteres"
                                                required
                                            />

                                            <button
                                                type="button"
                                                onClick={() =>
                                                    setMostrarSenha(
                                                        (valor) =>
                                                            !valor,
                                                    )
                                                }
                                            >
                                                {mostrarSenha
                                                    ? 'Ocultar'
                                                    : 'Mostrar'}
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="admin-form-section">
                                <div className="admin-form-section-heading">
                                    <span>02</span>

                                    <div>
                                        <strong>
                                            Dados acadêmicos
                                        </strong>

                                        <p>
                                            Informações acadêmicas do
                                            estudante.
                                        </p>
                                    </div>
                                </div>

                                <div className="admin-form-grid">
                                    <div className="admin-form-group">
                                        <label htmlFor="matricula">
                                            Matrícula
                                        </label>

                                        <input
                                            id="matricula"
                                            name="matricula"
                                            type="text"
                                            maxLength="30"
                                            value={
                                                formulario.matricula
                                            }
                                            onChange={alterarCampo}
                                            placeholder="Ex.: 20260003"
                                            required
                                        />
                                    </div>

                                    <div className="admin-form-group">
                                        <label htmlFor="semestre">
                                            Semestre
                                        </label>

                                        <input
                                            id="semestre"
                                            name="semestre"
                                            type="number"
                                            min="1"
                                            max="20"
                                            value={
                                                formulario.semestre
                                            }
                                            onChange={alterarCampo}
                                            placeholder="Ex.: 3"
                                            required
                                        />
                                    </div>

                                    <div className="admin-form-group admin-form-full">
                                        <label htmlFor="curso">
                                            Curso
                                        </label>

                                        <input
                                            id="curso"
                                            name="curso"
                                            type="text"
                                            maxLength="100"
                                            value={formulario.curso}
                                            onChange={alterarCampo}
                                            placeholder="Ex.: Análise e Desenvolvimento de Sistemas"
                                            required
                                        />
                                    </div>
                                </div>
                            </div>

                            {erro && (
                                <div className="admin-form-error">
                                    {erro}
                                </div>
                            )}

                            <div className="admin-form-actions">
                                <button
                                    type="button"
                                    className="admin-secondary-button"
                                    onClick={fecharFormulario}
                                    disabled={salvando}
                                >
                                    Cancelar
                                </button>

                                <button
                                    type="submit"
                                    className="admin-primary-button"
                                    disabled={salvando}
                                >
                                    {salvando
                                        ? 'Cadastrando...'
                                        : 'Cadastrar aluno'}
                                </button>
                            </div>
                        </form>
                    </section>
                </div>
            )}
        </div>
    )
}