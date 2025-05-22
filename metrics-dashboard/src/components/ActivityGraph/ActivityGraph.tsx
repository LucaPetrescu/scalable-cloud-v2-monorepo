import React from 'react';
import { SlGraph } from 'react-icons/sl';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';
import { ResponsiveContainer } from 'recharts';

const data = [
    {
        name: 'Page A',
        uv: 4000,
        pv: 2400,
        amt: 2400,
    },
    {
        name: 'Page B',
        uv: 3000,
        pv: 1398,
        amt: 2210,
    },
    {
        name: 'Page C',
        uv: 2000,
        pv: 9800,
        amt: 2290,
    },
    {
        name: 'Page D',
        uv: 2780,
        pv: 3908,
        amt: 2000,
    },
    {
        name: 'Page E',
        uv: 1890,
        pv: 4800,
        amt: 2181,
    },
    {
        name: 'Page F',
        uv: 2390,
        pv: 3800,
        amt: 2500,
    },
    {
        name: 'Page G',
        uv: 3490,
        pv: 4300,
        amt: 2100,
    },
];

export const ActivityGraph = () => {
    return (
        <div className="col-span-8 overflow-hidden rounded border border-stone-300">
            <div className="p-4">
                <h3 className="flex items-center gap-1.5 font-medium">
                    <SlGraph />
                    Activity
                </h3>
            </div>
            <div className="h-64 px-4">
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart
                        width={500}
                        height={400}
                        data={data}
                        margin={{
                            top: 0,
                            right: 0,
                            left: -24,
                            bottom: 0,
                        }}
                    >
                        <CartesianGrid stroke="#e4e4e7" />
                        <XAxis
                            dataKey="name"
                            axisLine={false}
                            tickLine={false}
                            className="text-xs font-bold"
                            padding={{ right: 4 }}
                        />
                        <YAxis className="text-xs font-bold" axisLine={false} tickLine={false} />
                        <Tooltip wrapperClassName="text-sm rounded" labelClassName="text-xs text-stone-500" />
                        <Line type="monotone" dataKey="New" stroke="#18181b" fill="#18181b" />
                        <Line type="monotone" dataKey="Returning" stroke="#5b21b6" fill="#5b21b6" />
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};
