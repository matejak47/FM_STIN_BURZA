function FavouritesTable({favourites, onToggleFavourite}) {
    return (
        <div className="favourites-table">
            {favourites.length === 0 ? (
                <p>No favourites yet.</p>
            ) : (
                <ul>
                    {favourites.map((symbol) => (
                        <li key={symbol} className="favourite-item">
                            <span className="favourite-symbol">{symbol}</span>
                            <button
                                className="remove-favourite-button"
                                onClick={() => onToggleFavourite(symbol)}
                                title="Odstranit z oblíbených"
                            >
                                –
                            </button>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    )
}

export default FavouritesTable