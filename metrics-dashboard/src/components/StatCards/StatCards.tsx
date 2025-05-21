import React from 'react';

export const StatCards = () => {
    return (
        <div className="grid grid-cols-12 gap-4">
            <Card />
            <Card />
            <Card />
            <Card />
            <Card />
        </div>
    );
};

const Card = () => {
    return <div className="bg-white rounded-lg shadow p-4">Card</div>;
};
