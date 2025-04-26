import React, {useEffect, useState} from "react";
import "./Portfolio.css";

const Portfolio = ({setBalance}) => {
    const [portfolio, setPortfolio] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchPortfolio = async () => {
        try {
            const response = await fetch("/api/trade/portfolio");
            if (!response.ok) throw new Error("Failed to fetch portfolio");

            const data = await response.json();
            setPortfolio(data);
            setLoading(false);
            setBalance(data.balance);
        } catch (error) {
            console.error("Error fetching portfolio:", error);
            setError("Failed to load portfolio.");
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchPortfolio();
    }, []);

    const handleSellStock = async (symbol) => {
        const quantityToSell = parseInt(prompt(`Enter quantity to sell for ${symbol}:`), 10);

        if (isNaN(quantityToSell) || quantityToSell <= 0) {
            alert("Invalid quantity!");
            return;
        }

        try {
            const response = await fetch("/api/trade/execute", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({symbol, orderType: "SELL", quantity: quantityToSell})
            });

            const result = await response.json();

            if (result.success) {
                await fetchPortfolio(); // ✅ Re-fetch portfolio po úspěšném prodeji
            } else {
                alert(result.message);
            }
        } catch (error) {
            console.error("Error executing sell order:", error);
            alert("Failed to execute sell order.");
        }
    };

    if (loading) return <p>Loading portfolio...</p>;
    if (error) return <p>{error}</p>;

    return (
        <div className="portfolio-content">
            <h1>Portfolio</h1>
            <p>Balance: ${portfolio.balance.toFixed(2)}</p>
            {Object.keys(portfolio.holdings).length === 0 ? (
                <p>No stocks purchased yet.</p>
            ) : (
                <table className="portfolio-table">
                    <thead>
                    <tr>
                        <th>Symbol</th>
                        <th>Quantity</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    {Object.entries(portfolio.holdings)
                        .filter(([_, quantity]) => quantity > 0)
                        .map(([symbol, quantity]) => (
                            <tr key={symbol}>
                                <td>{symbol}</td>
                                <td>{quantity}</td>
                                <td>
                                    <button className="sell-button" onClick={() => handleSellStock(symbol)}>
                                        Sell
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
};

export default Portfolio;
