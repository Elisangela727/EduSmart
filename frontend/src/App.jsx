import {
    Navigate,
    Route,
    Routes,
} from 'react-router-dom'

import Login from './pages/Login/Login'

import Dashboard from './pages/Dashboard/Dashboard'
import Notas from './pages/Notas/Notas'
import Frequencias from './pages/Frequencias/Frequencias'
import Analises from './pages/Analises/Analises'
import Alertas from './pages/Alertas/Alertas'

import AdminLayout from './components/AdminLayout/AdminLayout'
import AdminDashboard from './pages/Admin/Dashboard/AdminDashboard'
import AdminAlunos from './pages/Admin/Alunos/Alunos'
import AdminDisciplinas from './pages/Admin/Disciplinas/Disciplinas'
import AdminMatriculas from './pages/Admin/Matriculas/Matriculas'
import AdminNotas from './pages/Admin/Notas/Notas'
import AdminFrequencias from './pages/Admin/Frequencias/Frequencias'

import ProtectedRoute from './routes/ProtectedRoute'
import { useAuth } from './context/AuthContext'

export default function App() {
    const { autenticado, usuario, carregando } = useAuth()

    if (carregando) {
        return null
    }

    function protegerAluno(elemento) {
        return (
            <ProtectedRoute perfisPermitidos={['ALUNO']}>
                {elemento}
            </ProtectedRoute>
        )
    }

    function protegerAdmin(elemento) {
        return (
            <ProtectedRoute perfisPermitidos={['ADMIN']}>
                {elemento}
            </ProtectedRoute>
        )
    }

    function rotaInicial() {
        if (!autenticado) {
            return '/login'
        }

        if (usuario?.tipoUsuario === 'ADMIN') {
            return '/admin'
        }

        if (usuario?.tipoUsuario === 'ALUNO') {
            return '/dashboard'
        }

        return '/login'
    }

    return (
        <Routes>
            <Route
                path="/"
                element={
                    <Navigate
                        to={rotaInicial()}
                        replace
                    />
                }
            />

            <Route
                path="/login"
                element={<Login />}
            />

            <Route
                path="/dashboard"
                element={protegerAluno(<Dashboard />)}
            />

            <Route
                path="/notas"
                element={protegerAluno(<Notas />)}
            />

            <Route
                path="/frequencias"
                element={protegerAluno(<Frequencias />)}
            />

            <Route
                path="/analises"
                element={protegerAluno(<Analises />)}
            />

            <Route
                path="/alertas"
                element={protegerAluno(<Alertas />)}
            />

            <Route
                path="/admin"
                element={protegerAdmin(<AdminLayout />)}
            >
                <Route
                    index
                    element={<AdminDashboard />}
                />

                <Route
                    path="alunos"
                    element={<AdminAlunos />}
                />

                <Route
                    path="disciplinas"
                    element={<AdminDisciplinas />}
                />

                <Route
                    path="matriculas"
                    element={<AdminMatriculas />}
                />

                <Route
                    path="notas"
                    element={<AdminNotas />}
                />

                <Route
                    path="frequencias"
                    element={<AdminFrequencias />}
                />
            </Route>

            <Route
                path="*"
                element={
                    <Navigate
                        to="/"
                        replace
                    />
                }
            />
        </Routes>
    )
}