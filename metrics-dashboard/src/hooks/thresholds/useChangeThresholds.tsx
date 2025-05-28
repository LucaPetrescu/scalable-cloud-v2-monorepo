import axios from 'axios';

interface NewRuleDto {
    metricName: string;
    value: number;
}

export const useChangeThresholds = () => {
    const changeThresholds = async (serviceName: string, newRules: NewRuleDto[]) => {
        try {
            const response = await axios.post(`http://localhost:8085/rules/changeRules`, newRules, {
                params: { serviceName },
            });
            return response.data;
        } catch (error) {
            console.error('Error changing thresholds:', error);
            throw error;
        }
    };

    return { changeThresholds };
};
