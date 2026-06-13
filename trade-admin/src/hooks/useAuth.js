import { useState, useEffect } from 'react';
import { supabase } from '../supabaseClient';

/**
 * Hook to get the current authenticated user and their role using Supabase.
 */
export const useAuth = () => {
    const [user, setUser] = useState(null);
    const [role, setRole] = useState(null);
    const [loading, setLoading] = useState(true);

    const fetchRole = async (uid) => {
        try {
            const { data, error } = await supabase
                .from('profiles')
                .select('role')
                .eq('id', uid)
                .single();
            if (data && !error) {
                setRole(data.role);
            }
        } catch (error) {
            console.error("Error fetching user role:", error);
        }
    };

    useEffect(() => {
        // Get initial session
        const getSession = async () => {
            try {
                const { data: { session } } = await supabase.auth.getSession();
                if (session) {
                    setUser(session.user);
                    await fetchRole(session.user.id);
                }
            } catch (error) {
                console.error("Error getting session:", error);
            } finally {
                setLoading(false);
            }
        };

        getSession();

        // Listen for auth state changes
        const { data: { subscription } } = supabase.auth.onAuthStateChange(async (event, session) => {
            if (session) {
                setUser(session.user);
                await fetchRole(session.user.id);
            } else {
                setUser(null);
                setRole(null);
            }
            setLoading(false);
        });

        return () => {
            subscription.unsubscribe();
        };
    }, []);

    return { user, role, loading };
};
