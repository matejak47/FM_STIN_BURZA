import googlData from '../resources/data/googlData.json'

export async function fetchStockData(symbol) {
    // Pokud je symbol GOOGL, vrať lokální data z googlData.json
    if (symbol.toUpperCase() === 'GOOGL') {
        console.log('Používám testovací data pro GOOGL.')
        // Seřadíme kopii dat vzestupně podle data
        const sortedData = googlData.slice().sort((a, b) => new Date(a.date) - new Date(b.date))
        return sortedData
    }

    // Jinak zavoláme API
    const response = await fetch(`/api/burza/daily?symbol=${symbol}`)
    if (!response.ok) {
        throw new Error(`Chyba při načítání dat pro symbol ${symbol}`)
    }
    const data = await response.json()
    // Seřadíme data vzestupně
    data.sort((a, b) => new Date(a.date) - new Date(b.date))
    return data
}
