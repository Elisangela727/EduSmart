import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import './AdminLayout.css'

export default function AdminLayout() {
    const { usuario, logout } = useAuth()
    const navigate = useNavigate()

    function sair() {
        logout()
        navigate('/login')
    }

    function iniciais(nome) {
        if (!nome) {
            return 'AD'
        }

        return nome
            .split(' ')
            .slice(0, 2)
            .map((parte) => parte.charAt(0))
            .join('')
            .toUpperCase()
    }

    return (
        <div className="admin-layout">
            <header className="admin-header">
                <div className="admin-header-content">
                    <NavLink to="/admin" className="admin-brand">
                        <div className="admin-brand-icon">E</div>

                        <div>
                            <strong>EduSmart</strong>
                            <span>Administração Acadêmica</span>
                        </div>
                    </NavLink>

                    <nav className="admin-navigation">
                        <NavLink
                            to="/admin"
                            end
                            className={({ isActive }) =>
                                isActive ? 'admin-nav-active' : ''
                            }
                        >
                            Início
                        </NavLink>

                        <NavLink
                            to="/admin/alunos"
                            className={({ isActive }) =>
                                isActive ? 'admin-nav-active' : ''
                            }
                        >
                            Alunos
                        </NavLink>

                        <NavLink
                            to="/admin/disciplinas"
                            className={({ isActive }) =>
                                isActive ? 'admin-nav-active' : ''
                            }
                        >
                            Disciplinas
                        </NavLink>

                        <NavLink
                            to="/admin/matriculas"
                            className={({ isActive }) =>
                                isActive ? 'admin-nav-active' : ''
                            }
                        >
                            Matrículas
                        </NavLink>

                        <NavLink
                            to="/admin/notas"
                            className={({ isActive }) =>
                                isActive ? 'admin-nav-active' : ''
                            }
                        >
                            Notas
                        </NavLink>

                        <NavLink
                            to="/admin/frequencias"
                            className={({ isActive }) =>
                                isActive ? 'admin-nav-active' : ''
                            }
                        >
                            Frequências
                        </NavLink>
                    </nav>

                    <div className="admin-user-area">
                        <div className="admin-user-info">
                            <strong>{usuario?.nome}</strong>
                            <span>Administrador</span>
                        </div>

                        <div className="admin-avatar">
                            {iniciais(usuario?.nome)}
                        </div>

                        <button
                            type="button"
                            className="admin-logout-button"
                            onClick={sair}
                        >
                            Sair
                        </button>
                    </div>
                </div>
            </header>

            <main className="admin-main">
                <Outlet />
            </main>
        </div>
    )
}