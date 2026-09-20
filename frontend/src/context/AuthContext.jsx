import { createContext, useContext, useEffect, useState } from 'react'
import { post } from '../services/api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
    const [usuario, setUsuario] = useState(null)
    const [carregando, setCarregando] = useState(true)

    useEffect(() => {
        const usuarioSalvo = localStorage.getItem('edusmart_usuario')
        const tokenSalvo = localStorage.getItem('edusmart_token')

        if (usuarioSalvo && tokenSalvo) {
            try {
                setUsuario(JSON.parse(usuarioSalvo))
            } catch {
                localStorage.removeItem('edusmart_usuario')
                localStorage.removeItem('edusmart_token')
            }
        }

        setCarregando(false)
    }, [])

    async function login(email, senha) {
        const resposta = await post('/auth/login', {
            email,
            senha,
        })

        const usuarioLogado = {
            idUsuario: resposta.idUsuario,
            nome: resposta.nome,
            email: resposta.email,
            tipoUsuario: resposta.tipoUsuario,
        }

        localStorage.setItem('edusmart_token', resposta.token)
        localStorage.setItem(
            'edusmart_usuario',
            JSON.stringify(usuarioLogado),
        )

        setUsuario(usuarioLogado)

        return usuarioLogado
    }

    function logout() {
        localStorage.removeItem('edusmart_token')
        localStorage.removeItem('edusmart_usuario')
        setUsuario(null)
    }

    const autenticado = Boolean(usuario)

    return (
        <AuthContext.Provider
            value={{
                usuario,
                autenticado,
                carregando,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    const context = useContext(AuthContext)

    if (!context) {
        throw new Error(
            'useAuth deve ser utilizado dentro de AuthProvider',
        )
    }

    return context
}