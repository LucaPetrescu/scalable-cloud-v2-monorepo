import React from 'react';
import { Logo } from '../Logo/Logo.tsx';
import { Search } from '../Search/Search.tsx';
import { RouteSelect } from '../RouteSelect/RouteSelect.tsx';
import { Link } from 'react-router-dom';
export const Sidebar = () => {
    return (
        <div>
            <div className="overflow-y-scroll sticky top-4 h-[calc(100vh-32px-48px)]">
                <Link to="/">
                    <Logo />
                </Link>
                <Search />
                <RouteSelect />
                <Link to="/cache-dashboard" className="block mt-4 text-blue-600 hover:underline">
                    Redis Cache Dashboard
                </Link>
            </div>
        </div>
    );
};
