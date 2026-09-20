import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { get } from '../../../services/api'
import './AdminDashboard.css'

export default function AdminDashboard() {
    const [dados, setDados] = useState({
        alunos: 0,
        disciplinas: 0,
        matriculas: 0,
        notas: 0,
        frequencias: 0,
    })

    const [carregando, setCarregando] = useState(true)
    const [erro, setErro] = useState('')

    async function carregarDados() {
        try {
            setCarregando(true)
            setErro('')

            const [
                alunos,
                disciplinas,
                matriculas,
                notas,
                frequencias,
            ] = await Promise.all([
                get('/alunos'),
                get('/disciplinas'),
                get('/matriculas'),
                get('/notas'),
                get('/frequencias'),
            ])

            setDados({
                alunos: alunos?.length ?? 0,
                disciplinas: disciplinas?.length ?? 0,
                matriculas: matriculas?.length ?? 0,
                notas: notas?.length ?? 0,
                frequencias: frequencias?.length ?? 0,
            })
        } catch (error) {
            setErro(error.message)
        } finally {
            setCarregando(false)
        }
    }

    useEffect(() => {
        carregarDados()
    }, [])

    if (carregando) {
        return (
            <div className="admin-dashboard-loading">
                <div className="admin-dashboard-spinner" />
                <span>Carregando painel administrativo...</span>
            </div>
        )
    }

    return (
        <div className="admin-dashboard">
            <section className="admin-dashboard-welcome">
        <span className="admin-section-label">
          Administração acadêmica
        </span>

                <h1>Painel administrativo</h1>

                <p>
                    Gerencie alunos, disciplinas, matrículas, notas e
                    frequências do EduSmart.
                </p>
            </section>

            {erro && (
                <div className="admin-dashboard-error">
                    <div>
                        <strong>Não foi possível carregar os dados.</strong>
                        <p>{erro}</p>
                    </div>

                    <button type="button" onClick={carregarDados}>
                        Tentar novamente
                    </button>
                </div>
            )}

            <section className="admin-stat-grid">
                <article className="admin-stat-card">
                    <div className="admin-stat-heading">
                        <span>Alunos</span>
                        <div className="admin-stat-icon">A</div>
                    </div>

                    <strong>{dados.alunos}</strong>
                    <p>Alunos cadastrados</p>
                </article>

                <article className="admin-stat-card">
                    <div className="admin-stat-heading">
                        <span>Disciplinas</span>
                        <div className="admin-stat-icon">D</div>
                    </div>

                    <strong>{dados.disciplinas}</strong>
                    <p>Disciplinas cadastradas</p>
                </article>

                <article className="admin-stat-card">
                    <div className="admin-stat-heading">
                        <span>Matrículas</span>
                        <div className="admin-stat-icon">M</div>
                    </div>

                    <strong>{dados.matriculas}</strong>
                    <p>Vínculos acadêmicos</p>
                </article>

                <article className="admin-stat-card">
                    <div className="admin-stat-heading">
                        <span>Notas</span>
                        <div className="admin-stat-icon">N</div>
                    </div>

                    <strong>{dados.notas}</strong>
                    <p>Avaliações registradas</p>
                </article>

                <article className="admin-stat-card">
                    <div className="admin-stat-heading">
                        <span>Frequências</span>
                        <div className="admin-stat-icon">F</div>
                    </div>

                    <strong>{dados.frequencias}</strong>
                    <p>Registros de frequência</p>
                </article>
            </section>

            <section className="admin-actions-panel">
                <div className="admin-actions-heading">
          <span className="admin-section-label">
            Gerenciamento
          </span>
                    <h2>Acesso rápido</h2>
                    <p>
                        Escolha uma área para visualizar ou cadastrar
                        informações acadêmicas.
                    </p>
                </div>

                <div className="admin-actions-grid">
                    <Link to="/admin/alunos" className="admin-action-card">
                        <div className="admin-action-icon">A</div>
                        <div>
                            <strong>Gerenciar alunos</strong>
                            <span>
                Consulte e cadastre alunos no sistema.
              </span>
                        </div>
                        <b>→</b>
                    </Link>

                    <Link
                        to="/admin/disciplinas"
                        className="admin-action-card"
                    >
                        <div className="admin-action-icon">D</div>
                        <div>
                            <strong>Gerenciar disciplinas</strong>
                            <span>
                Consulte e cadastre disciplinas.
              </span>
                        </div>
                        <b>→</b>
                    </Link>

                    <Link
                        to="/admin/matriculas"
                        className="admin-action-card"
                    >
                        <div className="admin-action-icon">M</div>
                        <div>
                            <strong>Gerenciar matrículas</strong>
                            <span>
                Vincule alunos às disciplinas.
              </span>
                        </div>
                        <b>→</b>
                    </Link>

                    <Link to="/admin/notas" className="admin-action-card">
                        <div className="admin-action-icon">N</div>
                        <div>
                            <strong>Lançar notas</strong>
                            <span>
                Registre avaliações dos alunos.
              </span>
                        </div>
                        <b>→</b>
                    </Link>

                    <Link
                        to="/admin/frequencias"
                        className="admin-action-card"
                    >
                        <div className="admin-action-icon">F</div>
                        <div>
                            <strong>Lançar frequências</strong>
                            <span>
                Registre a presença dos alunos.
              </span>
                        </div>
                        <b>→</b>
                    </Link>
                </div>
            </section>
        </div>
    )
}