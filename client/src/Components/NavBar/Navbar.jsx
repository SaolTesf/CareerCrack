import { React } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { FaHome, FaBriefcase, FaClipboardList, FaCog } from "react-icons/fa";
import './Navbar.css';

export const Navbar = () => {
  const location = useLocation();
  let selectedTab = location.pathname;

  return (
    <nav className='navbar'>
      <Link to={"/home"} className={selectedTab==="/home" ? "selected": ''}>
        <FaHome />
        <span>Home</span>
      </Link>
      <Link to={"/problems"} className={selectedTab==="/problems" ? "selected": ''}>
        <FaClipboardList />
        <span>Problems</span>
      </Link>
      <Link to={"/applications"} className={selectedTab==="/applications" ? "selected": ''}>
        <FaBriefcase />
        <span>Applications</span>
      </Link>
      <Link to={"/settings"} className={selectedTab==="/settings" ? "selected": ''}>
        <FaCog />
        <span>Settings</span>
      </Link>
    </nav>
  )
}