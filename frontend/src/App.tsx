import { useEffect, useState } from 'react'
import axios from 'axios'
import './index.css'

function App() {
    const [message, setMessage] = useState<string>('Loading...')

    useEffect(() => {
        axios.get('/api/test-db')
            .then(response => {
                setMessage(response.data)
            })
            .catch(error => {
                console.error("Error fetching data:", error)
                setMessage('Error connecting to backend (check console)')
            })
    }, [])

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-900 text-white">
            <div className="text-center p-8 border border-gray-700 rounded-lg shadow-xl">
                <h1 className="text-3xl font-bold mb-4 text-blue-400">Overhearr System Test</h1>
                <p className="text-xl">
                    Result: <span className="font-mono text-green-400">{message}</span>
                </p>
            </div>
        </div>
    )
}

export default App