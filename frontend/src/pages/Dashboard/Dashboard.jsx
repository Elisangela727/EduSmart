import { useEffect, useState } from 'react'
import StudentLayout from '../../components/StudentLayout/StudentLayout'
import { get, patch } from '../../services/api'
import './Dashboard.css'

export default function Dashboard() {
    const [desempenho, setDesempenho] = useState(null)
    const [alertas, setAlertas] = useState([])
    const [carregando, setCarregando] = useState(true)
    const [erro, setErro] = useState('')

    useEffect(() => {
        carregarDashboard()
    }, [])

    async function carregarDashboard() {
        try {
            setCarregando(true)
            setErro('')

            const [dadosDesempenho, dadosAlertas] =
                await Promise.all([
                    get('/alunos/meu-desempenho'),
                    get('/alertas/meus'),
                ])

            setDesempenho(dadosDesempenho)
            setAlertas(dadosAlertas ?? [])
        } catch (error) {
            setErro(
                error.message ||
                'Não foi possível carregar o painel acadêmico.',
            )
        } finally {
            setCarregando(false)
        }
    }

    async function visualizarAlerta(idAlerta) {
        try {
            const alertaAtualizado = await patch(
                `/alertas/meus/${idAlerta}/visualizar`,
            )

            setAlertas((atuais) =>
                atuais.map((alerta) =>
                    alerta.idAlerta === idAlerta
                        ? alertaAtualizado
                        : alerta,
                ),
            )
        } catch (error) {
            setErro(
                error.message ||
                'Não foi possível atualizar o alerta.',
            )
        }
    }

    function formatarNumero(valor) {
        if (valor === null || valor === undefined) {
            return '--'
        }

        return Number(valor).toLocaleString('pt-BR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
        })
    }

    function formatarData(data) {
        if (!data) {
            return ''
        }

        const [ano, mes, dia] = data.split('-')
        return `${dia}/${mes}/${ano}`
    }

    function classeRisco(nivelRisco) {
        if (!nivelRisco) {
            return 'risco-neutro'
        }

        switch (nivelRisco.toUpperCase()) {
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
            <main className="dashboard-content">
                {carregando ? (
                    <div className="dashboard-inner-loading">
                        <div className="loading-spinner" />
                        <p>Carregando seu painel acadêmico...</p>
                    </div>
                ) : (
                    <>
                        <section className="dashboard-welcome">
                            <div>
                                <span className="section-label">
                                    Painel acadêmico
                                </span>

                                <h1>
                                    Olá, {desempenho?.nome || 'Aluno'}!
                                </h1>

                                <p>
                                    Acompanhe suas informações acadêmicas
                                    e seu desempenho.
                                </p>
                            </div>

                            {desempenho && (
                                <div className="student-summary">
                                    <span>{desempenho.curso}</span>

                                    <strong>
                                        {desempenho.semestre}º semestre
                                    </strong>

                                    <small>
                                        Matrícula {desempenho.matricula}
                                    </small>
                                </div>
                            )}
                        </section>

                        {erro && (
                            <div
                                className="dashboard-error"
                                role="alert"
                            >
                                <div>
                                    <strong>
                                        Não foi possível concluir a operação.
                                    </strong>
                                    <p>{erro}</p>
                                </div>

                                <button
                                    type="button"
                                    onClick={carregarDashboard}
                                >
                                    Tentar novamente
                                </button>
                            </div>
                        )}

                        {desempenho && (
                            <>
                                <section className="metric-grid">
                                    <article className="metric-card">
                                        <div className="metric-heading">
                                            <span>Média geral</span>
                                            <div className="metric-icon">
                                                M
                                            </div>
                                        </div>

                                        <strong>
                                            {formatarNumero(
                                                desempenho.mediaGeral,
                                            )}
                                        </strong>

                                        <p>
                                            Desempenho acadêmico geral
                                        </p>
                                    </article>

                                    <article className="metric-card">
                                        <div className="metric-heading">
                                            <span>
                                                Frequência média
                                            </span>
                                            <div className="metric-icon">
                                                F
                                            </div>
                                        </div>

                                        <strong>
                                            {formatarNumero(
                                                desempenho.frequenciaMedia,
                                            )}
                                            %
                                        </strong>

                                        <p>
                                            Presença nas disciplinas
                                        </p>
                                    </article>

                                    <article className="metric-card">
                                        <div className="metric-heading">
                                            <span>
                                                Risco acadêmico
                                            </span>
                                            <div className="metric-icon">
                                                R
                                            </div>
                                        </div>

                                        <strong
                                            className={`risk-value ${classeRisco(
                                                desempenho.nivelRisco,
                                            )}`}
                                        >
                                            {desempenho.nivelRisco
                                                    ?.replace('_', ' ') ||
                                                'Não analisado'}
                                        </strong>

                                        <p>
                                            Análise baseada no seu
                                            desempenho
                                        </p>
                                    </article>

                                    <article className="metric-card">
                                        <div className="metric-heading">
                                            <span>Disciplinas</span>
                                            <div className="metric-icon">
                                                D
                                            </div>
                                        </div>

                                        <strong>
                                            {desempenho.disciplinas
                                                ?.length ?? 0}
                                        </strong>

                                        <p>
                                            Disciplinas em acompanhamento
                                        </p>
                                    </article>
                                </section>

                                <div className="dashboard-grid">
                                    <section className="dashboard-panel">
                                        <div className="panel-header">
                                            <div>
                                                <span className="section-label">
                                                    Desempenho
                                                </span>
                                                <h2>
                                                    Minhas disciplinas
                                                </h2>
                                            </div>
                                        </div>

                                        {desempenho.disciplinas?.length >
                                        0 ? (
                                            <div className="disciplines-table-wrapper">
                                                <table className="disciplines-table">
                                                    <thead>
                                                    <tr>
                                                        <th>
                                                            Disciplina
                                                        </th>
                                                        <th>
                                                            Média
                                                        </th>
                                                        <th>
                                                            Frequência
                                                        </th>
                                                    </tr>
                                                    </thead>

                                                    <tbody>
                                                    {desempenho.disciplinas.map(
                                                        (
                                                            disciplina,
                                                        ) => (
                                                            <tr
                                                                key={
                                                                    disciplina.idDisciplina
                                                                }
                                                            >
                                                                <td>
                                                                    <div className="discipline-name">
                                                                        <div className="discipline-icon">
                                                                            {disciplina.nomeDisciplina
                                                                                ?.charAt(
                                                                                    0,
                                                                                )
                                                                                ?.toUpperCase()}
                                                                        </div>

                                                                        <span>
                                                                                {
                                                                                    disciplina.nomeDisciplina
                                                                                }
                                                                            </span>
                                                                    </div>
                                                                </td>

                                                                <td>
                                                                    {formatarNumero(
                                                                        disciplina.mediaNotas,
                                                                    )}
                                                                </td>

                                                                <td>
                                                                    {formatarNumero(
                                                                        disciplina.frequencia,
                                                                    )}
                                                                    %
                                                                </td>
                                                            </tr>
                                                        ),
                                                    )}
                                                    </tbody>
                                                </table>
                                            </div>
                                        ) : (
                                            <div className="empty-state">
                                                Nenhuma disciplina
                                                encontrada.
                                            </div>
                                        )}
                                    </section>

                                    <section className="dashboard-panel">
                                        <div className="panel-header">
                                            <div>
                                                <span className="section-label">
                                                    Acompanhamento
                                                </span>
                                                <h2>Alertas</h2>
                                            </div>

                                            <span className="alert-counter">
                                                {
                                                    alertas.filter(
                                                        (alerta) =>
                                                            !alerta.visualizado,
                                                    ).length
                                                }{' '}
                                                novo(s)
                                            </span>
                                        </div>

                                        {alertas.length > 0 ? (
                                            <div className="alerts-list">
                                                {alertas.map(
                                                    (alerta) => (
                                                        <article
                                                            className={`alert-item ${
                                                                alerta.visualizado
                                                                    ? 'alert-read'
                                                                    : ''
                                                            }`}
                                                            key={
                                                                alerta.idAlerta
                                                            }
                                                        >
                                                            <div className="alert-top">
                                                                <span
                                                                    className={`alert-risk ${classeRisco(
                                                                        alerta.tipoAlerta?.replace(
                                                                            'RISCO_',
                                                                            '',
                                                                        ),
                                                                    )}`}
                                                                >
                                                                    {alerta.tipoAlerta?.replace(
                                                                        'RISCO_',
                                                                        'Risco ',
                                                                    )}
                                                                </span>

                                                                <span className="alert-date">
                                                                    {formatarData(
                                                                        alerta.dataAlerta,
                                                                    )}
                                                                </span>
                                                            </div>

                                                            <p>
                                                                {
                                                                    alerta.mensagem
                                                                }
                                                            </p>

                                                            {!alerta.visualizado && (
                                                                <button
                                                                    type="button"
                                                                    onClick={() =>
                                                                        visualizarAlerta(
                                                                            alerta.idAlerta,
                                                                        )
                                                                    }
                                                                >
                                                                    Marcar
                                                                    como
                                                                    visualizado
                                                                </button>
                                                            )}
                                                        </article>
                                                    ),
                                                )}
                                            </div>
                                        ) : (
                                            <div className="empty-state">
                                                Você não possui alertas.
                                            </div>
                                        )}
                                    </section>
                                </div>
                            </>
                        )}
                    </>
                )}
            </main>
        </StudentLayout>
    )
}