import {useEffect, useState} from "react";
import "./LogViewer.css"; // Import CSS souboru

export default function LogViewer() {
    const [logs, setLogs] = useState([]);

    const fetchLogs = async () => {
        try {
            const response = await fetch("/api/logs");
            if (response.ok) {
                const data = await response.json();
                setLogs(data);
            } else {
                console.error("Failed to fetch logs");
            }
        } catch (error) {
            console.error("Error fetching logs:", error);
        }
    };

    useEffect(() => {
        fetchLogs();
        const interval = setInterval(fetchLogs, 5000); // Refresh every 5 seconds
        return () => clearInterval(interval);
    }, []);

    return (
        <div className="log-viewer-container">
            <div className="log-header">
                <h2>Server Logs</h2>
            </div>
            <ul className="log-list">
                {logs.map((log, index) => (
                    <li key={index} className="log-item">
                        {log}
                    </li>
                ))}
            </ul>
        </div>
    );
}
