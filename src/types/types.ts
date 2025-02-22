type User = { name: string; email: string; password: string };
type PasswordlessUser = {
    id: number;
    name: string;
    email: string;
};

type DBUser = { id: number; name: string; email: string; hashed_password: string };

export { User, PasswordlessUser, DBUser };
