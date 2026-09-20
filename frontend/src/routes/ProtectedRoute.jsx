import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function ProtectedRoute({ children, perfisPermitidos }) {
    const { usuario, autenticado, carregando } = useAuth()

    if (carregando) {
        return null
    }

    if (!autenticado) {
        return <Navigate to="/login" replace />
    }

    if (
        perfisPermitidos &&
        !perfisPermitidos.includes(usuario.tipoUsuario)
    ) {
        return <Navigate to="/login" replace />
    }

    return children
}