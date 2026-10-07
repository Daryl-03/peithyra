import { redirect } from "next/navigation";
import { requireOnboardedUser } from "@/features/identity/server/current-profile";

export async function GET() {
    await requireOnboardedUser();
    redirect("/agora");
}
