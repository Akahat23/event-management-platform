import React, { useState, useEffect, useContext } from 'react';
import axios from 'axios';
import { AuthContext } from './context/AuthContext';
import { 
  Calendar, 
  MapPin, 
  Users, 
  PlusCircle, 
  ClipboardList, 
  UserPlus, 
  Trash2, 
  Sparkles, 
  CheckCircle, 
  X,
  AlertCircle,
  LogIn,
  LogOut,
  UserCheck
} from 'lucide-react';

const API_BASE = 'http://localhost:8080/api';

function App() {
  const { user, token, login, register, logout, loading: authLoading } = useContext(AuthContext);

  const [events, setEvents] = useState([]);
  const [attendees, setAttendees] = useState([]);
  const [registrations, setRegistrations] = useState([]);
  const [activeTab, setActiveTab] = useState('events'); // 'events' | 'create' | 'attendees' | 'registrations' | 'auth'
  
  // Auth Form State
  const [isLoginView, setIsLoginView] = useState(true);
  const [authForm, setAuthForm] = useState({ name: '', email: '', password: '' });
  
  // Registration Form State
  const [showRegModal, setShowRegModal] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState(null);
  
  // Event Creation Form State
  const [eventForm, setEventForm] = useState({
    title: '',
    description: '',
    dateTime: '',
    location: '',
    capacity: ''
  });

  // UI state
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState(null); // { type: 'success'|'error', message: '' }

  // Set up Axios interceptors for handling 401/403 expired sessions
  useEffect(() => {
    const interceptor = axios.interceptors.response.use(
      response => response,
      error => {
        if (error.response && (error.response.status === 401 || error.response.status === 403)) {
          logout();
          setActiveTab('auth');
          showToast('error', 'Session expired or unauthorized. Please sign in again.');
        }
        return Promise.reject(error);
      }
    );
    return () => axios.interceptors.response.eject(interceptor);
  }, [logout]);

  useEffect(() => {
    fetchEvents();
    if (user && user.role === 'ROLE_ADMIN') {
      fetchAttendees();
      fetchRegistrations();
    }
  }, [user]);

  const showToast = (type, message) => {
    setNotification({ type, message });
    setTimeout(() => setNotification(null), 5000);
  };

  const fetchEvents = async () => {
    setLoading(true);
    try {
      const res = await axios.get(`${API_BASE}/events`);
      setEvents(res.data);
    } catch (err) {
      console.error(err);
      showToast('error', 'Failed to fetch events.');
    } finally {
      setLoading(false);
    }
  };

  const fetchAttendees = async () => {
    try {
      const res = await axios.get(`${API_BASE}/attendees`);
      setAttendees(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchRegistrations = async () => {
    try {
      const res = await axios.get(`${API_BASE}/registrations`);
      setRegistrations(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleAuthSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    let result;
    if (isLoginView) {
      result = await login(authForm.email, authForm.password);
    } else {
      result = await register(authForm.name, authForm.email, authForm.password);
    }
    setLoading(false);

    if (result.success) {
      showToast('success', isLoginView ? 'Welcome back!' : 'Account registered successfully!');
      setAuthForm({ name: '', email: '', password: '' });
      setActiveTab('events');
    } else {
      showToast('error', result.message);
    }
  };

  const handleCreateEvent = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await axios.post(`${API_BASE}/events`, {
        ...eventForm,
        capacity: parseInt(eventForm.capacity)
      });
      showToast('success', 'Event published successfully!');
      setEventForm({ title: '', description: '', dateTime: '', location: '', capacity: '' });
      fetchEvents();
      setActiveTab('events');
    } catch (err) {
      console.error(err);
      const msg = err.response?.data?.message || 
                  (err.response?.data?.validationErrors ? Object.values(err.response.data.validationErrors).join(', ') : 'Failed to publish event.');
      showToast('error', msg);
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    if (!user) {
      showToast('error', 'You must log in to register for an event.');
      setActiveTab('auth');
      setShowRegModal(false);
      return;
    }

    setLoading(true);
    try {
      // Use logged in user name and email
      await axios.post(`${API_BASE}/registrations`, {
        eventId: selectedEvent.id,
        attendeeName: user.name,
        attendeeEmail: user.email
      });
      showToast('success', `Registered successfully for "${selectedEvent.title}"!`);
      setShowRegModal(false);
      fetchEvents();
      if (user.role === 'ROLE_ADMIN') {
        fetchAttendees();
        fetchRegistrations();
      }
    } catch (err) {
      console.error(err);
      const msg = err.response?.data?.message || 'Registration failed.';
      showToast('error', msg);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteEvent = async (id) => {
    if (!window.confirm('Are you sure you want to delete this event? This will remove all associated registrations.')) return;
    try {
      await axios.delete(`${API_BASE}/events/${id}`);
      showToast('success', 'Event deleted.');
      fetchEvents();
      if (user.role === 'ROLE_ADMIN') {
        fetchRegistrations();
        fetchAttendees();
      }
    } catch (err) {
      console.error(err);
      showToast('error', 'Failed to delete event.');
    }
  };

  const formatEventDate = (dateStr) => {
    const d = new Date(dateStr);
    return d.toLocaleString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const isAdmin = user && user.role === 'ROLE_ADMIN';

  return (
    <div className="min-h-screen p-4 md:p-8 flex flex-col items-center">
      {/* Toast Notification */}
      {notification && (
        <div className={`fixed top-4 right-4 z-50 flex items-center gap-2 px-4 py-3 rounded-lg shadow-xl border backdrop-blur-md transition-all duration-300 ${
          notification.type === 'success' 
            ? 'bg-emerald-950/80 text-emerald-300 border-emerald-500/30' 
            : 'bg-rose-950/80 text-rose-300 border-rose-500/30'
        }`}>
          {notification.type === 'success' ? <CheckCircle size={20} /> : <AlertCircle size={20} />}
          <span className="text-sm font-medium">{notification.message}</span>
        </div>
      )}

      {/* Header */}
      <header className="w-full max-w-6xl mb-12 flex flex-col md:flex-row justify-between items-center gap-6">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-indigo-600/20 border border-indigo-500/30 rounded-2xl text-indigo-400">
            <Sparkles size={32} />
          </div>
          <div>
            <h1 className="text-3xl font-bold tracking-tight">
              Event<span className="gradient-text font-black">Link</span>
            </h1>
            <p className="text-sm text-slate-400">Premium Event Management Platform</p>
          </div>
        </div>

        {/* Tab Navigation */}
        <div className="flex flex-col sm:flex-row items-center gap-4">
          <nav className="flex gap-1 p-1 bg-slate-950/40 border border-white/5 rounded-xl backdrop-blur-md">
            <button 
              onClick={() => setActiveTab('events')}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${
                activeTab === 'events' ? 'bg-indigo-600 text-white shadow-md' : 'text-slate-400 hover:text-white'
              }`}
            >
              All Events
            </button>
            
            {isAdmin && (
              <>
                <button 
                  onClick={() => setActiveTab('create')}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all flex items-center gap-1.5 ${
                    activeTab === 'create' ? 'bg-indigo-600 text-white shadow-md' : 'text-slate-400 hover:text-white'
                  }`}
                >
                  <PlusCircle size={16} /> Publish
                </button>
                <button 
                  onClick={() => setActiveTab('attendees')}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all flex items-center gap-1.5 ${
                    activeTab === 'attendees' ? 'bg-indigo-600 text-white shadow-md' : 'text-slate-400 hover:text-white'
                  }`}
                >
                  <Users size={16} /> Attendees
                </button>
                <button 
                  onClick={() => setActiveTab('registrations')}
                  className={`px-4 py-2 rounded-lg text-sm font-medium transition-all flex items-center gap-1.5 ${
                    activeTab === 'registrations' ? 'bg-indigo-600 text-white shadow-md' : 'text-slate-400 hover:text-white'
                  }`}
                >
                  <ClipboardList size={16} /> Bookings
                </button>
              </>
            )}
          </nav>

          {/* Profile Section */}
          <div className="flex items-center gap-2">
            {authLoading ? (
              <div className="w-8 h-8 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin"></div>
            ) : user ? (
              <div className="flex items-center gap-3 bg-indigo-950/30 border border-indigo-500/20 px-3 py-1.5 rounded-xl">
                <div className="flex flex-col items-end">
                  <span className="text-sm font-semibold text-slate-200">{user.name}</span>
                  <span className="text-xs text-indigo-400 font-bold uppercase tracking-wider">
                    {user.role === 'ROLE_ADMIN' ? 'Admin' : 'Attendee'}
                  </span>
                </div>
                <button 
                  onClick={() => {
                    logout();
                    showToast('success', 'Logged out.');
                    setActiveTab('events');
                  }}
                  className="p-1.5 bg-slate-900 hover:bg-slate-800 text-slate-400 hover:text-rose-400 rounded-lg border border-white/5 transition-all"
                  title="Log Out"
                >
                  <LogOut size={16} />
                </button>
              </div>
            ) : (
              <button
                onClick={() => setActiveTab('auth')}
                className={`px-4 py-2 rounded-xl text-sm font-semibold flex items-center gap-1.5 transition-all ${
                  activeTab === 'auth' 
                    ? 'bg-indigo-600 text-white shadow-md' 
                    : 'bg-indigo-950/40 border border-indigo-500/30 text-indigo-300 hover:bg-indigo-600 hover:text-white'
                }`}
              >
                <LogIn size={16} /> Sign In
              </button>
            )}
          </div>
        </div>
      </header>

      {/* Main Container */}
      <main className="w-full max-w-6xl flex-grow">
        
        {/* Statistics Cards */}
        {activeTab === 'events' && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
            <div className="glass-panel p-6 flex items-center justify-between">
              <div>
                <p className="text-sm font-semibold text-indigo-300 uppercase tracking-wider">Total Events</p>
                <h3 className="text-4xl font-extrabold mt-1">{events.length}</h3>
              </div>
              <Calendar size={40} className="text-indigo-400/50" />
            </div>
            <div className="glass-panel p-6 flex items-center justify-between">
              <div>
                <p className="text-sm font-semibold text-purple-300 uppercase tracking-wider">Total Registrations</p>
                <h3 className="text-4xl font-extrabold mt-1">{isAdmin ? registrations.length : 'Restricted'}</h3>
              </div>
              <UserPlus size={40} className="text-purple-400/50" />
            </div>
            <div className="glass-panel p-6 flex items-center justify-between">
              <div>
                <p className="text-sm font-semibold text-emerald-300 uppercase tracking-wider">Unique Attendees</p>
                <h3 className="text-4xl font-extrabold mt-1">{isAdmin ? attendees.length : 'Restricted'}</h3>
              </div>
              <Users size={40} className="text-emerald-400/50" />
            </div>
          </div>
        )}

        {loading && events.length === 0 ? (
          <div className="flex justify-center items-center py-20">
            <div className="w-12 h-12 border-4 border-indigo-500 border-t-transparent rounded-full animate-spin"></div>
          </div>
        ) : null}

        {/* Tab content: All Events List */}
        {activeTab === 'events' && (
          <div>
            {events.length === 0 ? (
              <div className="glass-panel p-12 text-center rounded-2xl border border-white/5">
                <Calendar className="mx-auto text-slate-500 mb-4" size={48} />
                <h3 className="text-xl font-bold">No Events Found</h3>
                <p className="text-slate-400 mt-2">Check back later for active events or log in to publish one.</p>
                {isAdmin && (
                  <button 
                    onClick={() => setActiveTab('create')}
                    className="gradient-button mt-6 px-6 py-2.5 rounded-xl"
                  >
                    Create Event
                  </button>
                )}
              </div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {events.map(event => {
                  const percent = Math.min(100, Math.round((event.registeredCount / event.capacity) * 100));
                  const isFull = event.registeredCount >= event.capacity;
                  return (
                    <div key={event.id} className="glass-card p-6 flex flex-col justify-between relative overflow-hidden">
                      {isFull && (
                        <div className="absolute top-0 right-0 bg-rose-600 text-white text-xs font-bold px-3 py-1 rounded-bl-lg uppercase tracking-wider">
                          Sold Out
                        </div>
                      )}
                      <div>
                        <h3 className="text-xl font-bold text-slate-100 mb-2 leading-tight">{event.title}</h3>
                        <p className="text-slate-400 text-sm mb-5 line-clamp-3">{event.description || 'No description provided.'}</p>
                        
                        <div className="space-y-2.5 text-sm text-slate-300 mb-6">
                          <div className="flex items-center gap-2">
                            <Calendar size={16} className="text-indigo-400" />
                            <span>{formatEventDate(event.dateTime)}</span>
                          </div>
                          <div className="flex items-center gap-2">
                            <MapPin size={16} className="text-indigo-400" />
                            <span className="truncate">{event.location}</span>
                          </div>
                          <div className="flex items-center gap-2">
                            <Users size={16} className="text-indigo-400" />
                            <span>{event.registeredCount} / {event.capacity} Registered</span>
                          </div>
                        </div>

                        {/* Capacity Progress Bar */}
                        <div className="w-full bg-slate-900 rounded-full h-2 mb-6 overflow-hidden">
                          <div 
                            className={`h-full rounded-full transition-all duration-500 ${
                              isFull ? 'bg-rose-500' : percent > 80 ? 'bg-amber-500' : 'bg-indigo-500'
                            }`}
                            style={{ width: `${percent}%` }}
                          ></div>
                        </div>
                      </div>

                      <div className="flex gap-2">
                        <button
                          onClick={() => {
                            if (!user) {
                              showToast('error', 'Please log in to register for events.');
                              setActiveTab('auth');
                            } else {
                              setSelectedEvent(event);
                              setShowRegModal(true);
                            }
                          }}
                          disabled={isFull}
                          className="flex-grow gradient-button py-2.5 rounded-xl text-sm flex items-center justify-center gap-1.5"
                        >
                          <UserPlus size={16} /> {isFull ? 'Seats Filled' : 'Register Now'}
                        </button>
                        
                        {isAdmin && (
                          <button
                            onClick={() => handleDeleteEvent(event.id)}
                            className="px-3 bg-red-950/40 hover:bg-red-900/60 border border-red-500/20 hover:border-red-500/50 text-red-400 rounded-xl transition-all"
                          >
                            <Trash2 size={16} />
                          </button>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        )}

        {/* Tab content: Authentication (Sign In/Register) */}
        {activeTab === 'auth' && (
          <div className="max-w-md mx-auto glass-panel p-8">
            <h2 className="text-2xl font-bold mb-2 flex items-center gap-2">
              <UserCheck className="text-indigo-400" /> {isLoginView ? 'Sign In' : 'Register Account'}
            </h2>
            <p className="text-slate-400 text-sm mb-6">
              {isLoginView ? 'Welcome back! Enter credentials to log in.' : 'Fill details below to create an account.'}
            </p>

            <form onSubmit={handleAuthSubmit} className="space-y-4">
              {!isLoginView && (
                <div className="flex flex-col">
                  <label>Full Name</label>
                  <input 
                    type="text" 
                    required
                    placeholder="e.g. Akshat Singh"
                    value={authForm.name}
                    onChange={e => setAuthForm({...authForm, name: e.target.value})}
                  />
                </div>
              )}

              <div className="flex flex-col">
                <label>Email Address</label>
                <input 
                  type="email" 
                  required
                  placeholder="e.g. akshat@example.com"
                  value={authForm.email}
                  onChange={e => setAuthForm({...authForm, email: e.target.value})}
                />
              </div>

              <div className="flex flex-col">
                <label>Password</label>
                <input 
                  type="password" 
                  required
                  placeholder="••••••••"
                  value={authForm.password}
                  onChange={e => setAuthForm({...authForm, password: e.target.value})}
                />
              </div>

              <button 
                type="submit" 
                disabled={loading}
                className="w-full gradient-button py-3 rounded-xl mt-4 flex items-center justify-center gap-1.5"
              >
                {loading ? 'Authenticating...' : isLoginView ? 'Login' : 'Create Account'}
              </button>
            </form>

            <div className="mt-6 text-center text-sm">
              <span className="text-slate-400">
                {isLoginView ? "Don't have an account? " : "Already have an account? "}
              </span>
              <button 
                onClick={() => setIsLoginView(!isLoginView)}
                className="text-indigo-400 hover:text-indigo-300 font-semibold underline"
              >
                {isLoginView ? 'Register here' : 'Login here'}
              </button>
            </div>
            
            {/* Quick Access Helper */}
            <div className="mt-8 p-4 bg-slate-950/40 border border-indigo-500/10 rounded-xl">
              <p className="text-xs font-bold text-indigo-400 uppercase tracking-wider mb-2">💡 Quick Test Credentials</p>
              <div className="space-y-1 text-xs text-slate-400">
                <div><span className="font-semibold text-slate-300">Admin Account:</span> admin@event.com / admin123</div>
                <div><span className="font-semibold text-slate-300">User Account:</span> user@event.com / user123</div>
              </div>
            </div>
          </div>
        )}

        {/* Tab content: Create Event (Admin Only) */}
        {activeTab === 'create' && isAdmin && (
          <div className="max-w-xl mx-auto glass-panel p-8">
            <h2 className="text-2xl font-bold mb-6 flex items-center gap-2">
              <PlusCircle className="text-indigo-400" /> Publish New Event
            </h2>
            <form onSubmit={handleCreateEvent} className="space-y-5">
              <div className="flex flex-col">
                <label>Event Title</label>
                <input 
                  type="text" 
                  required
                  placeholder="e.g. NextGen Web Summit"
                  value={eventForm.title}
                  onChange={e => setEventForm({...eventForm, title: e.target.value})}
                />
              </div>

              <div className="flex flex-col">
                <label>Description</label>
                <textarea 
                  rows="4"
                  placeholder="Briefly describe the topics, speakers, and schedule..."
                  value={eventForm.description}
                  onChange={e => setEventForm({...eventForm, description: e.target.value})}
                ></textarea>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="flex flex-col">
                  <label>Date and Time</label>
                  <input 
                    type="datetime-local" 
                    required
                    value={eventForm.dateTime}
                    onChange={e => setEventForm({...eventForm, dateTime: e.target.value})}
                  />
                </div>
                <div className="flex flex-col">
                  <label>Seat Limit (Capacity)</label>
                  <input 
                    type="number" 
                    required
                    min="1"
                    placeholder="e.g. 150"
                    value={eventForm.capacity}
                    onChange={e => setEventForm({...eventForm, capacity: e.target.value})}
                  />
                </div>
              </div>

              <div className="flex flex-col">
                <label>Location / Meeting Link</label>
                <input 
                  type="text" 
                  required
                  placeholder="e.g. Auditorium Hall A or Zoom Link"
                  value={eventForm.location}
                  onChange={e => setEventForm({...eventForm, location: e.target.value})}
                />
              </div>

              <button 
                type="submit" 
                disabled={loading}
                className="w-full gradient-button py-3 rounded-xl mt-4"
              >
                {loading ? 'Publishing...' : 'Publish Event'}
              </button>
            </form>
          </div>
        )}

        {/* Tab content: Attendees Directory (Admin Only) */}
        {activeTab === 'attendees' && isAdmin && (
          <div className="glass-panel p-6 overflow-hidden">
            <h2 className="text-2xl font-bold mb-6 flex items-center gap-2">
              <Users className="text-indigo-400" /> Attendee Directory
            </h2>
            {attendees.length === 0 ? (
              <p className="text-slate-400 text-center py-8">No attendees registered yet.</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="border-b border-white/10 text-indigo-300 font-semibold">
                      <th className="py-3 px-4">ID</th>
                      <th className="py-3 px-4">Name</th>
                      <th className="py-3 px-4">Email Address</th>
                    </tr>
                  </thead>
                  <tbody>
                    {attendees.map(a => (
                      <tr key={a.id} className="border-b border-white/5 hover:bg-white/2 transition-all">
                        <td className="py-3.5 px-4 font-mono text-slate-500">#{a.id}</td>
                        <td className="py-3.5 px-4 font-medium text-slate-200">{a.name}</td>
                        <td className="py-3.5 px-4 text-slate-400">{a.email}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {/* Tab content: Bookings Log (Admin Only) */}
        {activeTab === 'registrations' && isAdmin && (
          <div className="glass-panel p-6 overflow-hidden">
            <h2 className="text-2xl font-bold mb-6 flex items-center gap-2">
              <ClipboardList className="text-indigo-400" /> Event Bookings Log
            </h2>
            {registrations.length === 0 ? (
              <p className="text-slate-400 text-center py-8">No bookings log found.</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left border-collapse">
                  <thead>
                    <tr className="border-b border-white/10 text-indigo-300 font-semibold">
                      <th className="py-3 px-4">Booking ID</th>
                      <th className="py-3 px-4">Event Title</th>
                      <th className="py-3 px-4">Attendee Name</th>
                      <th className="py-3 px-4">Attendee Email</th>
                      <th className="py-3 px-4">Booking Date</th>
                    </tr>
                  </thead>
                  <tbody>
                    {registrations.map(r => (
                      <tr key={r.id} className="border-b border-white/5 hover:bg-white/2 transition-all">
                        <td className="py-3.5 px-4 font-mono text-slate-500">REG-{r.id}</td>
                        <td className="py-3.5 px-4 font-semibold text-slate-200">{r.eventTitle}</td>
                        <td className="py-3.5 px-4 text-slate-300">{r.attendeeName}</td>
                        <td className="py-3.5 px-4 text-slate-400">{r.attendeeEmail}</td>
                        <td className="py-3.5 px-4 text-slate-400 text-sm">{formatEventDate(r.registrationDate)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

      </main>

      {/* Register Modal (Role Protected Booking Confirmation) */}
      {showRegModal && selectedEvent && (
        <div className="fixed inset-0 z-50 bg-black/60 backdrop-blur-sm flex justify-center items-center p-4">
          <div className="glass-panel p-8 w-full max-w-md relative animate-in fade-in zoom-in-95 duration-200">
            <button 
              onClick={() => setShowRegModal(false)}
              className="absolute top-4 right-4 p-1.5 hover:bg-white/10 rounded-lg text-slate-400 hover:text-white transition-all"
            >
              <X size={18} />
            </button>
            <h3 className="text-xl font-bold mb-1">Confirm Registration</h3>
            <p className="text-sm text-indigo-300 mb-6 font-medium">Event: {selectedEvent.title}</p>
            
            <form onSubmit={handleRegister} className="space-y-4">
              <div className="p-4 bg-slate-900 border border-white/5 rounded-xl text-sm space-y-2.5 mb-2">
                <div>
                  <span className="text-slate-400">Booking Account:</span>
                  <span className="block font-semibold text-slate-200 mt-0.5">{user?.name}</span>
                </div>
                <div>
                  <span className="text-slate-400">Confirmation Email:</span>
                  <span className="block font-semibold text-slate-200 mt-0.5">{user?.email}</span>
                </div>
              </div>

              <p className="text-xs text-slate-400 leading-normal">
                By booking, a seat is reserved under your login profile name and email address. Seat limits are enforced.
              </p>

              <button 
                type="submit" 
                disabled={loading}
                className="w-full gradient-button py-3 rounded-xl mt-4 flex items-center justify-center gap-1.5"
              >
                {loading ? 'Processing...' : 'Confirm Booking'}
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
