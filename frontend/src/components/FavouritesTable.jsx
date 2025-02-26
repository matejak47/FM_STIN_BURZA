function FavouritesTable({favourites, onToggleFavourite}) {
    return (
        <div className="favourites-table">
            {favourites.length === 0 ? (
                <p>No favourites yet.</p>
            ) : (
                <table>
                    <thead>
                    <tr>
                        <th>Company name</th>
                        <th>Symbol</th>
                        <th>Quantity</th>
                        <th>Total Value</th>
                        <th></th>
                        {/* Sloupec pro tlačítko odebrání */}
                    </tr>
                    </thead>
                    <tbody>
                    {favourites.map((fav) => {
                        const displayedQuantity = fav.quantity > 0 ? fav.quantity : '–'
                        const displayedValue =
                            fav.quantity > 0 && fav.totalValue > 0
                                ? `$${fav.totalValue.toFixed(2)}`
                                : '–'

                        return (
                            <tr key={fav.symbol}>
                                <td>{fav.name}</td>
                                <td>{fav.symbol}</td>
                                <td>{displayedQuantity}</td>
                                <td>{displayedValue}</td>
                                <td>
                                    <button
                                        className="remove-favourite-button"
                                        onClick={() => onToggleFavourite(fav.symbol, fav.name)}
                                        title="Remove from favourites"
                                    >
                                        –
                                    </button>
                                </td>
                            </tr>
                        )
                    })}
                    </tbody>
                </table>
            )}
        </div>
    )
}

export default FavouritesTable
