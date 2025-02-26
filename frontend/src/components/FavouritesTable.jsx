import React, {useEffect} from 'react';

function FavouritesTable({favourites, portfolio, onSelectFavourite}) {
    useEffect(() => {
        console.log("Favourites:", favourites);
        console.log("Portfolio:", portfolio);
    }, [favourites, portfolio]);

    if (!portfolio) {
        return <p>Loading portfolio...</p>;
    }

    return (
        <div className="favourites-table">
            {favourites.length === 0 ? (
                <p>No favourites yet.</p>
            ) : (
                <table>
                    <thead>
                    <tr>
                        <th>Symbol</th>
                        <th>Quantity</th>
                        <th>Total Value</th>
                    </tr>
                    </thead>
                    <tbody>
                    {favourites.map((symbol) => {
                        const quantity = portfolio?.holdings?.[symbol] || 0;
                        const totalValue = quantity > 0 ? `$${(quantity * 175).toFixed(2)}` : '–';

                        return (
                            <tr key={symbol}>
                                <td onClick={() => onSelectFavourite(symbol)} style={{cursor: 'pointer'}}>
                                    {symbol}
                                </td>
                                <td>{quantity > 0 ? quantity : '–'}</td>
                                <td>{totalValue}</td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default FavouritesTable;
