import os
import sys
import csv
import matplotlib
import pandas as pd
import numpy as np
import plotnine as p9
import scipy.stats as statistic

from dataclasses import dataclass
from matplotlib import pyplot

@dataclass
class Config:
    root_dir_name: str
    out_dir_name: str
    save_results: bool
    show_results: bool
    force_read: bool
    pdf_width: int
    pdf_height: int

    def __init__(self, argv):
        self.show_results = False
        self.save_results = False
        self.force_read = False
        self.pdf_width = 400
        self.pdf_height = 200

        for arg in argv[1:]:
            match arg:
                case '--show':
                    self.show_results = True
                case '--save':
                    self.save_results = True
                case '--force':
                    self.force_read = True

        self.root_dir_name = '.'
        self.out_dir_name = self.root_dir_name + '/plot/'
        print(self.root_dir_name)


def set_graphics_options():
    pd.set_option('display.max_columns', None)
    pd.set_option('display.max_rows', None)
    pd.set_option('display.max_colwidth', None)

    font = {'size'   : 34}
    matplotlib.rc('font', **font)
    pyplot.rc('pdf',fonttype = 42)
    pyplot.rc('ps',fonttype = 42)


def create_out_dir():
    if not os.path.exists(config.out_dir_name):
        try:
            os.mkdir(config.out_dir_name)
        except OSError:
            print ('Failed to create output directory %s' % output_path)
            os.exit(-1)


def create_plot(name, p, config, factor):
    if config.show_results:
        p.show()

    if config.save_results:
        create_out_dir()
        file_name = config.out_dir_name + name + '.pdf'
        print('Writing ' + file_name)
        p.save(file_name)


def create_csv(df, name):
    if config.show_results:
        print(df)

    if config.save_results:
        create_out_dir()
        df.to_csv(config.out_dir_name + name + '.csv', index=False, sep = ';')


def create_table(df, name, config):
    table = df.style.format(decimal='.', thousands=',', precision=2, escape='latex').to_latex(multicol_align='c')

    if config.show_results:
        print(table)

    if config.save_results:
        create_out_dir()
        with open(config.out_dir_name + name + '.tex', 'w') as f:
            print(table, file=f)


def top(series):
    return series.iloc[0]


def prepare_data():
    dtypes = {
        'Timestamp': 'string',
        'UserID': 'int32',
        'TaskID': 'int32',
        'ProblemCount': 'int32',
        'FeedbackLevel': 'string',
        'CustomTaskID': 'int32',
        'AdvancedFeedback': 'boolean',
        'Problems': 'string',
    }

    statistics = pd.read_csv('statistic.csv', dtype=dtypes, sep = ',', usecols=range(8), lineterminator='\n')

    print('Number of unfiltered users: %d' % len(pd.unique(statistics['UserID'])))

    statistics['Timestamp'] = pd.to_datetime(statistics['Timestamp'],dayfirst=True,format='mixed')
    statistics['TimeSec'] = statistics['Timestamp'].astype(int)/10**9
    statistics['Day'] = statistics['Timestamp'].dt.day
    normalize_time(22, 14, statistics)
    normalize_time(22, 15, statistics)
    normalize_time(28, 14, statistics)
    normalize_time(28, 15, statistics)

    statistics['Time'] = statistics['TimeSec']/60

    statistics = statistics[statistics['TimeSec'] < 1800]

    # Filter users with max problem count = 0 in at least one task
    temp = statistics.loc[statistics.groupby(['UserID','TaskID'])['ProblemCount'].idxmax()]
    temp = temp[temp['ProblemCount'] == 0]
    errorUserIDs = pd.unique(temp['UserID'])

    # Filter users with only one task
    temp = statistics.groupby(['UserID']).nunique().reset_index()
    temp = temp[temp['TaskID'] != 2]
    errorUserIDs = np.append(errorUserIDs, pd.unique(temp['UserID']))

    errorUserStatistics = statistics[statistics['UserID'].isin(errorUserIDs)]
    errorUserStatistics = errorUserStatistics.sort_values(by=['UserID','TaskID'])
    print('Filtered Users:')
    print(len(pd.unique(errorUserStatistics['UserID'])))
    print(errorUserStatistics[['Timestamp','UserID','TaskID','ProblemCount','AdvancedFeedback']])

    statistics = statistics[-statistics['UserID'].isin(errorUserIDs)]

    statistics = statistics[statistics['ProblemCount'] > 0]

    statistics['UserTaskID'] = statistics['UserID'].astype('str') + '_' + statistics['TaskID'].astype('str')

    print('========================================')
    #print(statistics.head(30))
    statistics.info(verbose=True, memory_usage='deep')
    print('========================================')
    return statistics


def normalize_time(day, taskid, statistics):
    x = statistics[(statistics['Day'] == day) & (statistics['TaskID'] == taskid)]
    statistics.loc[(statistics['Day'] == day) & (statistics['TaskID'] == taskid),'TimeSec'] = x['TimeSec'] - min(x['TimeSec'])


if __name__ == '__main__':
    config = Config(sys.argv)
    set_graphics_options()
    df = prepare_data()

    print('Number of users: %d' % len(pd.unique(df['UserID'])))
    print('Number of users in Ulm: %d' % len(pd.unique(df[df['Day'] == 22]['UserID'])))
    print('Number of users in Braunschweig: %d' % len(pd.unique(df[df['Day'] == 28]['UserID'])))

    data1 = df.loc[df['AdvancedFeedback'] == True, 'ProblemCount']
    data2 = df.loc[df['AdvancedFeedback'] == False, 'ProblemCount']
    stat, p = statistic.ttest_ind(data1, data2)
    print('Mean problem count for advanced and not advanced feedback: stat=%.3f, p=%.3f' % (stat, p))

    df2 = df.groupby(['UserTaskID']).agg({
        'ProblemCount' : 'median',
        'AdvancedFeedback' : top}).reset_index().sort_values(by=['UserTaskID'])
    data1 = df2.loc[df2['AdvancedFeedback'] == True, 'ProblemCount']
    data2 = df2.loc[df2['AdvancedFeedback'] == False, 'ProblemCount']
    stat, p = statistic.ttest_rel(data1, data2)
    print('Median problem count for each user for advanced and not advanced feedback: stat=%.3f, p=%.3f' % (stat, p))

    p = (
    p9.ggplot(df, p9.aes('Time', 'ProblemCount', group='UserID'))
    + p9.geom_point()
    + p9.geom_line()
    + p9.labs(x='Time in Minutes', y='Problem Count')
    + p9.facet_wrap(['AdvancedFeedback'])
    )
    create_plot('problem_count_per_user', p, config, 1)

    df['TimeBins'] = ((np.floor(df['Time']/1) + 1) * 1).astype('int')
    df2 = df.groupby(['UserTaskID','TimeBins']).agg({
        'ProblemCount' : min,
        'AdvancedFeedback' : top,
        'Day' : top}).reset_index().sort_values(by=['TimeBins','UserTaskID'])

    p = (
    p9.ggplot(df2, p9.aes('TimeBins', 'ProblemCount', color='AdvancedFeedback'))
    + p9.geom_jitter()
    + p9.geom_smooth()
    + p9.labs(x='Time in Minutes', y='Problem Count', color='Filtered Feedback')
    + p9.theme(legend_position=(.05,.05))
    )
    create_plot('problem_count_smooth', p, config, 1)

    df['TimeBins'] = ((np.floor(df['Time']/3) + 1) * 3).astype('int')
    df2 = df.groupby(['UserTaskID','TimeBins']).agg({
        'ProblemCount' : min,
        'AdvancedFeedback' : top,
        'Day' : top}).reset_index().sort_values(by=['TimeBins','UserTaskID'])

    p = (
    p9.ggplot(df2, p9.aes('factor(TimeBins)', 'ProblemCount', color='AdvancedFeedback'))
    + p9.geom_boxplot()
    + p9.labs(x='Time in Minutes', y='Problem Count', color='Filtered Feedback')
    + p9.theme(legend_position=(.05,.05))
    )
    create_plot('problem_count_boxplot', p, config, 1)
