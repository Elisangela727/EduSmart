import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import './StudentLayout.css'

export default function StudentLayout({ children }) {
    const navigate = useNavigate()
    const { usuario, logout } = useAuth()

    function sair() {
        logout()
        navigate('/login', { replace: true })
    }

    return (
        <div className="student-layout">
            <header className="student-header">
                <div className="student-header-content">
                    <NavLink
                        to="/dashboard"
                        className="student-brand"
                    >
                        <div className="student-brand-icon">
                            E
                        </div>

                        <div>
                            <strong>EduSmart</strong>
                            <span>
                                Acompanhamento Acadêmico
                            </span>
                        </div>
                    </NavLink>

                    <nav className="student-navigation">
                        <NavLink
                            to="/dashboard"
                            className={({ isActive }) =>
                                isActive ? 'nav-active' : ''
                            }
                        >
                            Início
                        </NavLink>

                        <NavLink
                            to="/notas"
                            className={({ isActive }) =>
                                isActive ? 'nav-active' : ''
                            }
                        >
                            Notas
                        </NavLink>

                        <NavLink
                            to="/frequencias"
                            className={({ isActive }) =>
                                isActive ? 'nav-active' : ''
                            }
                        >
                            Frequência
                        </NavLink>

                        <NavLink
                            to="/analises"
                            className={({ isActive }) =>
                                isActive ? 'nav-active' : ''
                            }
                        >
                            Análises
                        </NavLink>

                        <NavLink
                            to="/alertas"
                            className={({ isActive }) =>
                                isActive ? 'nav-active' : ''
                            }
                        >
                            Alertas
                        </NavLink>
                    </nav>

                    <div className="student-user-area">
                        <div className="student-user-info">
                            <strong>{usuario?.nome}</strong>
                            <span>{usuario?.email}</span>
                        </div>

                        <div className="student-avatar">
                            {usuario?.nome
                                ?.charAt(0)
                                ?.toUpperCase()}
                        </div>

                        <button
                            type="button"
                            className="student-logout"
                            onClick={sair}
                        >
                            Sair
                        </button>
                    </div>
                </div>
            </header>

            {children}
        </div>
    )
}