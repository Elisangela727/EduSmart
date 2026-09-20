import { useState } from 'react'
import { Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import './Login.css'

export default function Login() {
    const navigate = useNavigate()
    const { login, logout, autenticado, usuario } = useAuth()

    const [email, setEmail] = useState('')
    const [senha, setSenha] = useState('')
    const [erro, setErro] = useState('')
    const [enviando, setEnviando] = useState(false)
    const [mostrarSenha, setMostrarSenha] = useState(false)

    if (autenticado) {
        if (usuario?.tipoUsuario === 'ALUNO') {
            return <Navigate to="/dashboard" replace />
        }

        if (usuario?.tipoUsuario === 'ADMIN') {
            return <Navigate to="/admin" replace />
        }
    }

    async function handleSubmit(event) {
        event.preventDefault()

        setErro('')
        setEnviando(true)

        try {
            const usuarioLogado = await login(
                email.trim(),
                senha,
            )

            if (usuarioLogado.tipoUsuario === 'ALUNO') {
                navigate('/dashboard', { replace: true })
                return
            }

            if (usuarioLogado.tipoUsuario === 'ADMIN') {
                navigate('/admin', { replace: true })
                return
            }

            logout()

            setErro(
                'Este perfil ainda não possui uma área disponível no sistema.',
            )
        } catch (error) {
            setErro(
                error.message ||
                'Não foi possível realizar o login.',
            )
        } finally {
            setEnviando(false)
        }
    }

    return (
        <main className="login-page">
            <section className="login-presentation">
                <div className="brand">
                    <div className="brand-icon">E</div>

                    <div>
                        <strong>EduSmart</strong>
                        <span>Acompanhamento Acadêmico</span>
                    </div>
                </div>

                <div className="presentation-content">
                    <span className="presentation-label">
                        Tecnologia aplicada à educação
                    </span>

                    <h1>
                        Acompanhe seu desempenho de forma inteligente.
                    </h1>

                    <p>
                        Consulte notas, frequência, desempenho acadêmico,
                        análises de risco e alertas em um único ambiente.
                    </p>

                    <div className="feature-list">
                        <div className="feature-item">
                            <span>01</span>
                            <p>Notas e frequência centralizadas</p>
                        </div>

                        <div className="feature-item">
                            <span>02</span>
                            <p>Análise acadêmica com Machine Learning</p>
                        </div>

                        <div className="feature-item">
                            <span>03</span>
                            <p>Alertas para acompanhamento do desempenho</p>
                        </div>
                    </div>
                </div>

                <p className="presentation-footer">
                    EduSmart • Sistema Inteligente de Acompanhamento Acadêmico
                </p>
            </section>

            <section className="login-area">
                <div className="login-card">
                    <div className="login-header">
                        <span className="mobile-brand">EduSmart</span>

                        <h2>Bem-vindo</h2>

                        <p>
                            Entre com seus dados para acessar sua conta.
                        </p>
                    </div>

                    <form onSubmit={handleSubmit}>
                        <div className="form-group">
                            <label htmlFor="email">E-mail</label>

                            <input
                                id="email"
                                type="email"
                                placeholder="seuemail@exemplo.com"
                                value={email}
                                onChange={(event) =>
                                    setEmail(event.target.value)
                                }
                                autoComplete="email"
                                required
                            />
                        </div>

                        <div className="form-group">
                            <label htmlFor="senha">Senha</label>

                            <div className="password-field">
                                <input
                                    id="senha"
                                    type={mostrarSenha ? 'text' : 'password'}
                                    placeholder="Digite sua senha"
                                    value={senha}
                                    onChange={(event) =>
                                        setSenha(event.target.value)
                                    }
                                    autoComplete="current-password"
                                    required
                                />

                                <button
                                    type="button"
                                    className="show-password"
                                    onClick={() =>
                                        setMostrarSenha((valor) => !valor)
                                    }
                                >
                                    {mostrarSenha ? 'Ocultar' : 'Mostrar'}
                                </button>
                            </div>
                        </div>

                        {erro && (
                            <div className="login-error" role="alert">
                                {erro}
                            </div>
                        )}

                        <button
                            className="login-button"
                            type="submit"
                            disabled={enviando}
                        >
                            {enviando ? 'Entrando...' : 'Entrar'}
                        </button>
                    </form>

                    <p className="login-support">
                        Problemas para acessar? Entre em contato com a
                        administração acadêmica.
                    </p>
                </div>
            </section>
        </main>
    )
}