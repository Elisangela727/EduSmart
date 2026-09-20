const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export async function apiRequest(endpoint, options = {}) {
    const token = localStorage.getItem('edusmart_token')

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
    }

    if (token) {
        headers.Authorization = `Bearer ${token}`
    }

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        headers,
    })

    let data = null

    const contentType = response.headers.get('content-type')

    if (contentType?.includes('application/json')) {
        data = await response.json()
    }

    if (!response.ok) {
        const mensagem =
            data?.mensagem ||
            data?.message ||
            data?.erro ||
            'Não foi possível concluir a solicitação.'

        throw new Error(mensagem)
    }

    return data
}

export function get(endpoint) {
    return apiRequest(endpoint)
}

export function post(endpoint, body) {
    return apiRequest(endpoint, {
        method: 'POST',
        body: JSON.stringify(body),
    })
}

export function patch(endpoint, body) {
    return apiRequest(endpoint, {
        method: 'PATCH',
        body: body ? JSON.stringify(body) : undefined,
    })
}