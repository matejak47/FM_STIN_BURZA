function FavouritesTable({ favourites }) {
    return (
        <div className="favourites-table">
            {favourites.length === 0 ? (
                <p>No favourites yet.</p>
            ) : (
                <ul>
                    {favourites.map((symbol) => (
                        <li key={symbol}>{symbol}</li>
                    ))}
                </ul>
            )}
        </div>
    )
}

export default FavouritesTable
