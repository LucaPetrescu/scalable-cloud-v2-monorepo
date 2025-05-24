import { useEffect, useState } from 'react';
import axios from 'axios';

export const useThresholds = (serviceName: string) => {
    const [thresholds, setThresholds] = useState([]);

    useEffect(() => {
        const fetchThresholds = async () => {
            const response = await axios.get(`http://localhost:8085/rules/getRulesForService`, {
                params: { serviceName },
            });
            setThresholds(response.data);
        };
        fetchThresholds();
    }, [serviceName]);

    return thresholds;
};
