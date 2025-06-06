import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Sidebar } from '../components/Sidebar/Sidebar.tsx';

interface AlertEntry {
    key: string;
    reason: string;
    metricName: string;
    metricValue: number;
}

const SimpleTable = ({ data }: { data: AlertEntry[] }) => {
    if (!Array.isArray(data) || data.length === 0) {
        return <div className="text-center text-gray-500 py-8">No alerts available.</div>;
    }

    return (
        <div className="overflow-x-auto mt-6 rounded-lg shadow border border-gray-200 bg-white">
            <table className="min-w-full divide-y divide-gray-200">
                <thead>
                    <tr>
                        <th className="px-6 py-3 bg-gray-50 text-left text-xs font-bold text-gray-700 uppercase tracking-wider border-b">
                            Reason
                        </th>
                        <th className="px-6 py-3 bg-gray-50 text-left text-xs font-bold text-gray-700 uppercase tracking-wider border-b">
                            Metric
                        </th>
                        <th className="px-6 py-3 bg-gray-50 text-left text-xs font-bold text-gray-700 uppercase tracking-wider border-b">
                            Value
                        </th>
                        <th className="px-6 py-3 bg-gray-50 text-left text-xs font-bold text-gray-700 uppercase tracking-wider border-b">
                            Last Updated
                        </th>
                    </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-100">
                    {data.map((alert, idx) => (
                        <tr key={idx} className="hover:bg-red-50 transition-colors">
                            <td className="px-6 py-3 whitespace-nowrap text-sm text-gray-800 border-b">
                                {alert.reason}
                            </td>
                            <td className="px-6 py-3 whitespace-nowrap text-sm text-gray-800 border-b">
                                {alert.metricName}
                            </td>
                            <td className="px-6 py-3 whitespace-nowrap text-sm text-gray-800 border-b">
                                {alert.metricValue.toFixed(2)}
                            </td>
                            <td className="px-6 py-3 whitespace-nowrap text-sm text-gray-800 border-b">
                                {new Date().toLocaleTimeString()}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

const CacheDashboard = () => {
    const [alerts, setAlerts] = useState<AlertEntry[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [lastUpdated, setLastUpdated] = useState<Date | null>(null);

    const fetchAlerts = async () => {
        try {
            setLoading(true);
            const response = await axios.get('http://localhost:8085/cache/alerts');
            setAlerts(response.data);
            console.log('response dataalerts', response.data);
            setLastUpdated(new Date());
            setError(null);
        } catch (err) {
            console.error('Error fetching alerts:', err);
            setError('Failed to fetch alerts. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAlerts();
        // Set up auto-refresh every 30 seconds
        const intervalId = setInterval(fetchAlerts, 30000);
        return () => clearInterval(intervalId);
    }, []);

    return (
        <main className="grid gap-4 p-4 grid-cols-[220px,_1fr]">
            <Sidebar />
            <div className="p-8">
                <div className="flex justify-between items-center mb-4">
                    <h1 className="text-2xl font-bold">Alert Cache Dashboard</h1>
                    <div className="flex items-center gap-4">
                        {lastUpdated && (
                            <span className="text-sm text-gray-500">
                                Last updated: {lastUpdated.toLocaleTimeString()}
                            </span>
                        )}
                        <button
                            onClick={fetchAlerts}
                            disabled={loading}
                            className="px-4 py-2 bg-red-600 text-white rounded shadow hover:bg-red-700 disabled:opacity-50 transition-colors"
                        >
                            {loading ? 'Refreshing...' : 'Refresh'}
                        </button>
                    </div>
                </div>

                {error && <div className="mb-4 p-4 bg-red-50 border border-red-200 text-red-700 rounded">{error}</div>}

                <SimpleTable data={alerts} />
            </div>
        </main>
    );
};

export default CacheDashboard;
