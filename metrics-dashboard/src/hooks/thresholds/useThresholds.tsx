import { useEffect, useState } from 'react';
import axios from 'axios';

export const useThresholds = (serviceName: string | undefined) => {
    const [thresholds, setThresholds] = useState([]);

    useEffect(() => {
        const fetchThresholds = async () => {
            if (!serviceName) {
                setThresholds([]);
                return;
            }

            try {
                const response = await axios.get(`http://localhost:8085/rules/getRulesForService`, {
                    params: { serviceName },
                });

                if (response.data && Array.isArray(response.data) && response.data.length > 1) {
                    setThresholds(response.data || []);
                } else {
                    console.warn('Invalid thresholds data received:', response.data);
                    setThresholds([]);
                }
            } catch (error) {
                console.error('Error fetching thresholds:', error);
                setThresholds([]);
            }
        };

        fetchThresholds();
    }, [serviceName]);

    return thresholds;
};
