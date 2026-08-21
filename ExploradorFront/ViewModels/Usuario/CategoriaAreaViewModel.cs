namespace ExploradorFront.ViewModels.Usuario;

public enum CategoriaAreaViewModel
{
    Computacion,
    Matematicas,
    Fisica,
    Quimica,
    Biologia,
    Medicina,
    Ingenieria,
    CienciasTierra,
    Economia,
    Estadistica
}

public static class CategoriaAreaInfo
{
    public static string Nombre(CategoriaAreaViewModel categoria) => categoria switch
    {
        CategoriaAreaViewModel.Computacion => "Computacion",
        CategoriaAreaViewModel.Matematicas => "Matematicas",
        CategoriaAreaViewModel.Fisica => "Fisica",
        CategoriaAreaViewModel.Quimica => "Quimica",
        CategoriaAreaViewModel.Biologia => "Biologia",
        CategoriaAreaViewModel.Medicina => "Medicina",
        CategoriaAreaViewModel.Ingenieria => "Ingenieria",
        CategoriaAreaViewModel.CienciasTierra => "Ciencias de la Tierra",
        CategoriaAreaViewModel.Economia => "Economia",
        CategoriaAreaViewModel.Estadistica => "Estadistica",
        _ => throw new ArgumentOutOfRangeException(nameof(categoria), categoria, null)
    };

    public static IReadOnlyList<string> Arxiv(CategoriaAreaViewModel categoria) => categoria switch
    {
        CategoriaAreaViewModel.Computacion => ["cs.AI", "cs.LG", "cs.CL", "cs.CV", "cs.SE", "cs.PL"],
        CategoriaAreaViewModel.Matematicas => ["math.NT", "math.NA", "math.AP", "math.CO", "math.OC"],
        CategoriaAreaViewModel.Fisica => ["physics.class-ph", "physics.comp-ph", "physics.flu-dyn"],
        CategoriaAreaViewModel.Quimica => ["physics.chem-ph", "cond-mat.mtrl-sci"],
        CategoriaAreaViewModel.Biologia => ["q-bio.BM", "q-bio.GN", "q-bio.QM"],
        CategoriaAreaViewModel.Medicina => [],
        CategoriaAreaViewModel.Ingenieria => ["eess.SY", "eess.SP", "cs.RO"],
        CategoriaAreaViewModel.CienciasTierra => ["physics.geo-ph"],
        CategoriaAreaViewModel.Economia => ["econ.EM", "econ.GN"],
        CategoriaAreaViewModel.Estadistica => ["stat.ML", "stat.ME", "stat.AP"],
        _ => throw new ArgumentOutOfRangeException(nameof(categoria), categoria, null)
    };
}
