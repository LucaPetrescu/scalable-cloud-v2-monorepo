import React from 'react';
import { Logo } from '../Logo/Logo.tsx';
export const Sidebar = () => {
    return <div>
       <div className="overflow-y-scroll sticky top-4 h-[calc(100vh-32px-48px)]">
        <Logo />
       </div>
    </div>
}
